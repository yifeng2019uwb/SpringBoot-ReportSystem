package com.yifeng.reporting_system.service;

import com.yifeng.reporting_system.Exception.ApplicationException;
import com.yifeng.reporting_system.pojo.report.ExcelData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ExcelGenerationService {



    File generateExcelReport(ExcelData data) throws IOException;

    InputStream getExcelBodyById(String id) throws ApplicationException;

    String deleteFile(String filePath) throws ApplicationException;

}
