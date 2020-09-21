package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface ExcelGenerationService {

    ExcelData createExcelData(ExcelRequest request, String splitBy) throws ParseException;

    ExcelFile createExcelFile(File file, ExcelData data);

    File generateExcelReport(ExcelData data) throws IOException;

//    File generateMultiSheetExcelReport(ExcelData data) throws IOException, ParseException;

}
