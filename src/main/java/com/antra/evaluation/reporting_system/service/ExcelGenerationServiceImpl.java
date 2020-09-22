package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.Exception.ApplicationException;
import com.antra.evaluation.reporting_system.Exception.BadRequestException;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.util.*;


/**
 * Data Stucture
 * data - title, generatedTime
 * - sheets
 * -sheet1 - title (required)
 * - headers
 * - name
 * - width
 * - type
 * - dataRows
 * - List of objects/values
 */

@Service
public class ExcelGenerationServiceImpl implements ExcelGenerationService {

    @Autowired
    ExcelRepository excelRepository;

    private void validateData(ExcelData data) {

        if(data.getTitle() == null) {
            throw new BadRequestException("No file Name");
        }
        if (data.getSheets().size() < 1) {
            throw new BadRequestException("Excel Data Error: no sheet is defined");
        }
        for (ExcelDataSheet sheet : data.getSheets()) {
            if (StringUtils.isEmpty(sheet.getTitle())) {
                throw new BadRequestException("Excel Data Error: sheet name is missing");
            }
            if(sheet.getHeaders() != null) {
                int columns = sheet.getHeaders().size();
                for (List<Object> dataRow : sheet.getDataRows()) {
                    if (dataRow.size() != columns) {
                        throw new BadRequestException("Excel Data Error: sheet data has difference length than header number");
                    }
                }
            }
        }
    }

    // valid splitBy if the splitBy key word are same as any column (header)
    private void validateSplitBy(List<ExcelDataHeader> headers, String splitBy) {

        boolean valid = false;
        for (ExcelDataHeader header : headers) {
            if (header.getName().equals(splitBy)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            throw new BadRequestException("Excel Data Split Error: split sheet not working");
        }

    }



    @Override
    public File generateExcelReport(ExcelData data) throws IOException {
        validateData(data);
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);


        for (ExcelDataSheet sheetData : data.getSheets()) {
            Sheet sheet = workbook.createSheet(sheetData.getTitle());

            Row header = sheet.createRow(0);
            List<ExcelDataHeader> headersData = sheetData.getHeaders();
            for (int i = 0; i < headersData.size(); i++) {
                ExcelDataHeader headerData = headersData.get(i);
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(headerData.getName());
                if(headerData.getWidth() > 0) sheet.setColumnWidth(i, headerData.getWidth());
                headerCell.setCellValue(headerData.getName());
                headerCell.setCellStyle(headerStyle);
            }
            var rowData = sheetData.getDataRows();
            for (int i = 0; i < rowData.size(); i++) {
                Row row = sheet.createRow(1 + i);
                var eachRow = rowData.get(i);
                for (int j = 0; j < eachRow.size(); j++) {
                    Cell cell = row.createCell(j);
//                    switch (headersData.get(j).getType()) {
//                        case STRING:cell.setCellValue(String.valueOf(eachRow.get(j))); cell.setCellType(CellType.STRING);break;
//                        case NUMBER: cell.setCellValue(eachRow.get(j));cell.setCellType(CellType.NUMERIC);break;
//                        case DATE:cell.setCellValue((Date)eachRow.get(j));break;
//                        default:cell.setCellValue(String.valueOf(eachRow.get(j)));break;
//                    }
                    cell.setCellValue(String.valueOf(eachRow.get(j)));
                    cell.setCellStyle(style);
                }
            }
            for (int i = 0; i < headersData.size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        // TODO : file name cannot be hardcoded here,
        // Save all the file and add random code to avoid duplicate filename
        String fileLocation = path.substring(0, path.length() - 1) + UUID.randomUUID().toString() + data.getTitle()+".xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        try {
            // thread safe
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(fileLocation);
    }

    @Override
    public synchronized InputStream getExcelBodyById(String id) throws ApplicationException {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()) {
            File file = new File(fileInfo.get().getDownloadLink());
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
                throw new ApplicationException("No file found", HttpStatus.NOT_FOUND);
            }
        }

        return null;
    }

    @Override
    public String deleteFile(String filePath)  {

        File file = new File(filePath);
        boolean deleted = false;

        try {
            deleted = file.delete();
        } catch (Exception ex) {
            throw new ApplicationException("No file found");
        }

        return deleted ? filePath:null;
    }

}
