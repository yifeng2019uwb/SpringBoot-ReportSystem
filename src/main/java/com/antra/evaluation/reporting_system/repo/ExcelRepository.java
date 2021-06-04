package com.yifeng.reporting_system.repo;

import com.yifeng.reporting_system.pojo.report.ExcelFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ExcelRepository {
    Optional<ExcelFile> getFileById(String id);

    ExcelFile saveFile(ExcelFile file);

    List<String> getListofFiles();

    List<ExcelFile> getFiles();

    ExcelFile getFileInfo(String id);

    boolean findFile(String fileName);

    ExcelFile removeRecord(String fieldId);

}
