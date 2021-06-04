package com.yifeng.reporting_system.service;

import com.yifeng.reporting_system.Exception.ApplicationException;
import com.yifeng.reporting_system.pojo.report.ExcelData;
import com.yifeng.reporting_system.pojo.report.ExcelFile;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public interface ExcelService {

    InputStream getExcelBodyById(String id) throws ApplicationException;

    File generateExcelFile(ExcelData data) throws IOException;

    File generateAutoFile(ExcelData data, @NonNull String splitBy) throws IOException, ParseException;

    List<String> getAllFileList();

    String deleteFile(String id) throws ApplicationException;

    ExcelFile getFileInfo(String id);

//    ExcelFile createExcelFile(File file, ExcelData data);

    ExcelFile saveFileRecord(File file, ExcelData data);



}
