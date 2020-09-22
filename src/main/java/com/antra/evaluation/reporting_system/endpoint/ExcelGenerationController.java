package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.Exception.ApplicationException;
import com.antra.evaluation.reporting_system.Exception.BadRequestException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@CrossOrigin("*")
@Slf4j
public class ExcelGenerationController {

    private final ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }


    @PostMapping(path = "/excel",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json; charset=UTF-8")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {

        ExcelResponse response = new ExcelResponse();
        try {
            ExcelData data = new ExcelData(request);
            File file = excelService.generateExcelFile(data);
            ExcelFile excelFile = excelService.saveFileRecord(file, data);
            response.setFileId(excelFile.getFieldId());
            response.setMessgae("file generated successfully!");
        } catch (Exception ex) {
            log.error("Bad request");
            System.out.println("bad bad");
            throw new BadRequestException("Bad data");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/excel/auto",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json; charset=UTF-8")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) {
        ExcelResponse response = new ExcelResponse();
        try {
            ExcelData data = new ExcelData(request);
            File file = excelService.generateAutoFile(data, request.getSplitBy());
            ExcelFile excelFile = excelService.saveFileRecord(file, data);
            response.setFileId(excelFile.getFieldId());
            response.setMessgae("multi-sheet file generated successfully!");
        } catch (Exception ex) {
            log.error("Bad request");
            throw new BadRequestException("Bad data");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {

        var response = new ArrayList<ExcelResponse>();

        List<String> files = excelService.getAllFileList();
        if (files.size() > 0) {
            for (String f : files) {
                ExcelResponse exresp = new ExcelResponse();
                exresp.setFileId(f);
                response.add((exresp));
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel/{id}/content")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {

        InputStream fis = excelService.getExcelBodyById(id);
        if (fis == null) {
            log.info("cannot find the file");
            throw new ApplicationException("File Not Found", HttpStatus.NOT_FOUND);
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".xlsx\"");
        log.info("download excel file: " + id + ".xlsx");
        FileCopyUtils.copy(fis, response.getOutputStream());
        fis.close();

    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) throws ApplicationException {
        var response = new ExcelResponse();

        try {
            String deleted = excelService.deleteFile(id);
            log.info("delete file " + id + ".xlsx");
            response.setFileId(id);
        } catch (ApplicationException ex) {
                log.info("Fail to delete file " + id + ".xlsx");
//            throw new FileNotFoundException() - BAD_REQUEST
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(path = "/excel/batch",
            consumes = "application/json",
            headers = "Accept=application/json",
            produces = "application/json; charset=UTF-8")
    @ApiOperation("Generate Multi Excel Using json array")
    public ResponseEntity<List<ExcelResponse>> createMultiSheetExcel(@RequestBody @Validated List<ExcelRequest> request) {

        var responses = new ArrayList<ExcelResponse>();

        for (ExcelRequest req : request) {
            try {
                ExcelData data = new ExcelData(req);
                File file = excelService.generateExcelFile(data);
                ExcelFile excelFile = excelService.saveFileRecord(file, data);

                var resp = new ExcelResponse();
                resp.setFileId(excelFile.getFieldId());
                responses.add(resp);
            } catch (Exception ex) {
                log.info("Bad request for batch");
                throw new BadRequestException("Bad data");
            }
        }
        log.info("save batch files: total " + responses.size() + "files");
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    @GetMapping("/excel/downloadZip")
    public void downloadMultiExcels(@RequestParam List<String> ids, HttpServletResponse response)  {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        int i = 0;

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (i = 0; i < ids.size(); i++) {
                InputStream fis = excelService.getExcelBodyById(ids.get(i));
                if (fis == null) {
                    log.info("cannot find the file");
                    throw new ApplicationException("File Not Found", HttpStatus.NOT_FOUND);
                }

                ExcelFile excelFile = excelService.getFileInfo(ids.get(i));
                ZipEntry e = new ZipEntry(excelFile.getFileName());
                e.setSize(excelFile.getFileSize());
                e.setTime(System.currentTimeMillis());
                zippedOut.putNextEntry(e);
                StreamUtils.copy(fis, zippedOut);
                fis.close();
                zippedOut.closeEntry();
            }
            zippedOut.finish();
            log.info("zip all downloading files");
        } catch (Exception e) {
            log.info(ids.get(i) + "File is not Found");
            throw new ApplicationException("File Not Found", HttpStatus.NOT_FOUND);
        }

    }

    // Log
// Exception handling
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void badRequest(BadRequestException ex) {

    }


// / Validation

}


