package com.antra.evaluation.reporting_system.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public interface ExcelService {

    InputStream getExcelBodyById(String id) throws FileNotFoundException;

    List<String> getFileNames(List<String> ids) throws FileNotFoundException;

}
