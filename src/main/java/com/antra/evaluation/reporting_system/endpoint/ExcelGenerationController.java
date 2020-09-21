package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.Exception.ApplicationException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@CrossOrigin("*")
@Slf4j
public class ExcelGenerationController {

//    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);
//    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ExcelRepository excelRepository;
    private final ExcelGenerationService excelGenerationService;
    private final ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService,
                                     ExcelGenerationService excelGenerationService,
                                     ExcelRepository excelRepository) {
        this.excelService = excelService;
        this.excelGenerationService = excelGenerationService;
        this.excelRepository = excelRepository;

    }


    @PostMapping(path = "/excel",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel( @RequestBody @Validated ExcelRequest request) throws IOException, ParseException {

        // for test
        ExcelData data = excelGenerationService.createExcelData(request, "");
//        System.out.println("in generation : " + data.toString());

        ExcelResponse response = new ExcelResponse();
        try {
            File file = excelGenerationService.generateExcelReport(data);
            excelRepository.saveFile(excelGenerationService.createExcelFile(file, data));

//            response.setFileId(file.getPath());
            response.setFileId(data.getTitle());

        }catch (Exception ex){
            throw new ApplicationException("file is not found", HttpStatus.OK);
        }

//        response.setFileId(data.getTitle());
        log.info("save single sheet excel file ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/excel/auto",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json; charset=UTF-8")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws ParseException, IOException {
        ExcelData data = excelGenerationService.createExcelData(request, request.getSplitBy());
        File file = excelGenerationService.generateExcelReport(data);

        excelRepository.saveFile(excelGenerationService.createExcelFile(file, data));

        ExcelResponse response = new ExcelResponse();
//        response.setFileId(file.getPath());
        response.setFileId(data.getTitle());
        log.info("save multi-sheet excel file" + file.getPath());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {

        var response = new ArrayList<ExcelResponse>();

        List<ExcelFile> files = excelRepository.getFiles();
        if (files.size() > 0) {
            for (ExcelFile f : files) {
                ExcelResponse exresp = new ExcelResponse();
                // here can give the file name/not link
//                exresp.setFileId(f.getDownloadLink());
                exresp.setFileId(f.getField());
                response.add((exresp));
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // it already done and just change the file name, you can change it
    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {

        InputStream fis = null;
        // thread safety
        try {
            fis = excelService.getExcelBodyById(id);
            if (fis == null) {
                log.info("cannot find the file");
                throw new ApplicationException("File Not Found", HttpStatus.NOT_FOUND);

            }
            response.setHeader("Content-Type", "application/vnd.ms-excel");
            // TODO: File name cannot be hardcoded here
//        response.setHeader("Content-Disposition","attachment; filename=\"name_of_excel_file.xls\"");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".xlsx\"");
            log.info("download excel file: " + id + ".xlsx");
            FileCopyUtils.copy(fis, response.getOutputStream());
        } finally {
            fis.close();
        }

    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) throws FileNotFoundException {

        boolean deleted = excelRepository.deleteFile(id);
        if (deleted) {
            log.info("delete file " + id + ".xlsx");
        } else {
            log.info("Fail to delete file " + id + ".xlsx");
//            throw new FileNotFoundException();
            // BAD_REQUEST
            throw new ApplicationException("File Not Found");
        }

        excelRepository.removeRecord(id);
        var response = new ExcelResponse();
        response.setFileId(String.valueOf(deleted));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(path = "/excel/batch",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json; charset=UTF-8")
    @ApiOperation("Generate Multi Excel Using json array")
    public ResponseEntity<List<ExcelResponse>> createMultiSheetExcel(@RequestBody @Validated List<MultiSheetExcelRequest> request) throws ParseException, IOException {

        var responses = new ArrayList<ExcelResponse>();

        for (MultiSheetExcelRequest req : request) {
            String splitBy = req.getSplitBy();
            if (splitBy == null || splitBy.isEmpty() || splitBy.isBlank()) splitBy = "";
            ExcelData data = excelGenerationService.createExcelData(req, splitBy);
            File file = excelGenerationService.generateExcelReport(data);
            excelRepository.saveFile(excelGenerationService.createExcelFile(file, data));
            var resp = new ExcelResponse();
//            resp.setFileId(file.getPath());
            resp.setFileId(data.getTitle());
            responses.add(resp);
        }

        log.info("save batch files: total " + request.size() + "files");
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    //
    @GetMapping("/excel/downloadZip")
    public void downloadMultiExcels(@RequestParam List<String> ids, HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        // just use download.zip as file name
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        // get all files' path, in this function, throw fileNotFoundEexception if didn't find the path
        List<String> fileNames = excelService.getFileNames(ids);

        int i = 0;
        // Here we can use exceptionhander too, just try different way
        // throw exception if cannot get source and zip it
        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (i = 0; i < ids.size(); i++) {
                String file = fileNames.get(i);
                FileSystemResource resource = new FileSystemResource(file);

                ZipEntry e = new ZipEntry(resource.getFilename());
                // Configure the zip entry, the properties of the file
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());

                zippedOut.putNextEntry(e);
                // And the content of the resource:
                StreamUtils.copy(resource.getInputStream(), zippedOut);
                zippedOut.closeEntry();
            }
            zippedOut.finish();
            log.info("zip all downloading files");
        } catch (Exception e) {
            // Here we can use exceptionhander too, just try different way
            log.info(ids.get(i) + "File is not Found");
//            e.printStackTrace();
            throw new ApplicationException("File Not Found", HttpStatus.NOT_FOUND);
        }

    }

    // Log
// Exception handling
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noFileFound(ApplicationException ex) {

    }

// / Validation

}


