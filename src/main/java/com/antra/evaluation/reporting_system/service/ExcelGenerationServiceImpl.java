package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

        if (data.getSheets().size() < 1) {
//            System.out.println("get null data");
            throw new RuntimeException("Excel Data Error: no sheet is defined");
        }

        // validate data title == > But I choose save all data even if missing the filename
//        if (data.getTitle().isEmpty()) {
//            throw new RuntimeException("Excel Data Error: no filename");
//        }

        // I set the sheet title for all sheet
        for (ExcelDataSheet sheet : data.getSheets()) {
//            System.out.println("check date sheet");
            if (StringUtils.isEmpty(sheet.getTitle())) {
                throw new RuntimeException("Excel Data Error: sheet name is missing");
            }
            if (sheet.getHeaders() != null) {
//                System.out.println("get datasheet header = " + sheet.getHeaders());
                int columns = sheet.getHeaders().size();
                for (List<Object> dataRow : sheet.getDataRows()) {
                    if (dataRow.size() != columns) {
                        throw new RuntimeException("Excel Data Error: sheet data has difference length than header number");
                    }
                }
            }
        }

        // validate the cell value is the type as the excelDataType set by header
        // But the performance will be too bad. don't do it here

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
            throw new RuntimeException("Excel Data Split Error: split sheet not working");
        }

    }


    @Override
    public ExcelData createExcelData(ExcelRequest request, String splitBy) throws ParseException {

        ExcelData data = new ExcelData();

        data.setGeneratedTime(LocalDateTime.now());
        // if (distinct) fileName required, validate the fileName here
        // but I choose to save the file with giving random name if filename is not available
        // also giving random title to the depulicate filename ==> do it when save into repository
        // because the content may be different even have the same file name
        // It not worth to compare two file with all data.
        String fileName = request.getFilename();
        if (fileName == null ) {
            UUID tid = UUID.randomUUID();
            fileName = tid.toString();
        }
        data.setTitle(fileName);

        // ExcelData has multisheet as default
        List<ExcelDataSheet> multiSheets = new ArrayList<>();
        ExcelDataSheet sheet = new ExcelDataSheet();

        List<ExcelDataHeader> hds = request.getHeaders();

        // validate splitBy
        if (!splitBy.isEmpty()) validateSplitBy(hds, splitBy);

        if (splitBy.isEmpty()) {
            // single sheet
            ExcelDataSheet singleSheet = new ExcelDataSheet();
            // didn't have name on json data, use filename
            singleSheet.setTitle(fileName);
            singleSheet.setHeaders(hds);
            for (List<String> row : request.getData()) {
                singleSheet.addDataRow(row);
            }
            multiSheets.add(singleSheet);
        } else {
            // multi sheet
            int splitIndex = -1;
            for (int i = 0; i < hds.size(); i++) {
                if (hds.get(i).getName().equals(splitBy)) {
                    splitIndex = i;
                    break;
                }
            }
            Map<String, ExcelDataSheet> map = new HashMap<>();
            for (List<String> row : request.getData()) {
                String sheetKey = row.get(splitIndex);
                if (!map.containsKey(sheetKey)) {
                    ExcelDataSheet newSheet = new ExcelDataSheet();
                    newSheet.setHeaders(hds);
                    newSheet.setTitle(sheetKey);
                    map.put(sheetKey, newSheet);
                }
                map.get(sheetKey).addDataRow(row);

            }
            for (String key : map.keySet()) {
                multiSheets.add(map.get(key));
            }
        }

        data.setSheets(multiSheets);

        return data;
    }

    @Override
    public ExcelFile createExcelFile(File file, ExcelData data) {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setGeneratedTime(data.getGeneratedTime());
        String filePath = file.getPath().toString();
        String[] paths = filePath.split("/");
        String fileName = paths[paths.length - 1].substring(0, paths[paths.length - 1].length() - 5);
        excelFile.setField(fileName);

        excelFile.setDownloadLink(file.getPath());
        excelFile.setFilesize(file.getTotalSpace());
        return excelFile;

    }

    @Override
    public File generateExcelReport(ExcelData data) throws IOException {

//        if (data == null) return null;
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
                if (headerData.getWidth() > 0) sheet.setColumnWidth(i, headerData.getWidth());
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
        // TODO : file name cannot be hardcoded here
        // String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
        String fileName = data.getTitle();

        if (fileName == null || excelRepository.findFile(fileName)) {
            UUID id = UUID.randomUUID();
            fileName = fileName + id.toString();
        }
        String fileLocation = path.substring(0, path.length() - 1) + fileName + ".xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        // thread safe
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(fileLocation);
    }


}
