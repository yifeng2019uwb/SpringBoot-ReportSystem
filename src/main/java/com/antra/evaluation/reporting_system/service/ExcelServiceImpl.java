package com.yifeng.reporting_system.service;

import com.yifeng.reporting_system.Exception.ApplicationException;
import com.yifeng.reporting_system.Exception.BadRequestException;
import com.yifeng.reporting_system.pojo.report.ExcelData;
import com.yifeng.reporting_system.pojo.report.ExcelDataHeader;
import com.yifeng.reporting_system.pojo.report.ExcelDataSheet;
import com.yifeng.reporting_system.repo.ExcelRepository;
import com.yifeng.reporting_system.pojo.report.ExcelFile;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService excelGenerationService;

    @Override
    public File generateExcelFile(ExcelData data) throws IOException {
        return excelGenerationService.generateExcelReport(data);
    }

    private int validateSplitBy(List<ExcelDataHeader> headers, String splitBy) {

        if (headers == null) return -1;
        int splitIndex = -1;
        for (int i = 0; i < headers.size(); i++) {

            if (headers.get(i).getName().equals(splitBy)) {
                splitIndex = i;
                break;
            }
        }
        return splitIndex;
    }

    @Override
    public File generateAutoFile(ExcelData data, @NonNull String splitBy) throws IOException, BadRequestException {


        List<ExcelDataSheet> splitedSheet = new ArrayList<>();
        for (ExcelDataSheet sheet : data.getSheets()) {
            List<ExcelDataHeader> headers = sheet.getHeaders();
            int splitIndex = validateSplitBy(headers, splitBy);
            if (splitIndex == -1) {
//                log.info("Bad split keyword");
                throw new BadRequestException("Bad split key word");
            } else {
                Map<String, Integer> map = new HashMap<>();
                for (List<Object> row : sheet.getDataRows()) {
                    String sheetKey = row.get(splitIndex).toString();

                    if (!map.containsKey(sheetKey)) {
                        ExcelDataSheet newSheet = new ExcelDataSheet();
                        newSheet.setHeaders(headers);
                        newSheet.setTitle(sheet.getTitle() + "-" + splitBy + "-" + sheetKey);
                        newSheet.setDataRows(new ArrayList<>());

                        map.put(sheetKey, splitedSheet.size());
                        splitedSheet.add(newSheet);
                    }
                    splitedSheet.get(map.get(sheetKey)).getDataRows().add(row);

                }
            }
        }

        data.setSheets(splitedSheet);
        return generateExcelFile(data);

    }


    @Override
    public InputStream getExcelBodyById(String id) throws ApplicationException {

        return excelGenerationService.getExcelBodyById(id);
    }


    @Override
    public List<String> getAllFileList() {
        return excelRepository.getListofFiles();
    }

    @Override
    public String deleteFile(String id) throws ApplicationException {

        ExcelFile fileInfo = excelRepository.getFileInfo(id);
//        if (fileInfo == null ) {
//            throw new ApplicationException("File not found", HttpStatus.NOT_FOUND);
//        }

        String deleted = excelGenerationService.deleteFile(fileInfo.getDownloadLink());
        excelRepository.removeRecord(fileInfo.getFieldId());

        return deleted;
    }


    @Override
    public ExcelFile getFileInfo(String id) {
        return excelRepository.getFileInfo(id);
    }

//    @Override
//    public ExcelFile createExcelFile(File file, ExcelData data) {
//        ExcelFile excelFile = new ExcelFile(file.getName(), data.getGeneratedTime(),file.getTotalSpace(),file.getAbsolutePath());
//
//        return excelFile;
//
//    }

    public ExcelFile saveFileRecord(File file, ExcelData data) {
        ExcelFile excelFile = new ExcelFile(file.getName(), data.getGeneratedTime(), file.getTotalSpace(), file.getAbsolutePath());
        excelRepository.saveFile(excelFile);
        return excelFile;

    }

}
