package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ExcelRepository {
    Optional<ExcelFile> getFileById(String id);

    ExcelFile saveFile(ExcelFile file);

    boolean deleteFile(String id);

    List<ExcelFile> getFiles();

    boolean findFile(String fileName);

    void removeRecord(String fileName);

}
