package com.yifeng.reporting_system.repo;


import com.yifeng.reporting_system.pojo.report.ExcelFile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();

    @Override
    public Optional<ExcelFile> getFileById(String id) {

        return Optional.ofNullable(excelData.get(id));
    }

    @Override
    public ExcelFile saveFile(ExcelFile file) {
        if(file == null) {
            return null;
        }

        excelData.put(file.getFieldId(), file);
        return file;
    }

    @Override
    public List<ExcelFile> getFiles() {
        List<ExcelFile> files = new ArrayList<>();
        for(String fname : excelData.keySet()) {
            files.add(excelData.get(fname));
        }
        return files;
    }

    @Override
    public boolean findFile(String id) {
        return excelData.containsKey(id);
    }

    @Override
    public ExcelFile removeRecord(String fieldId) {
            return excelData.remove(fieldId);
    }

    @Override
    public List<String> getListofFiles(){
        List<String> files = new ArrayList<>();
        for(String fname : excelData.keySet()) {
            files.add(fname);
        }
        return files;
    }

    @Override
    public ExcelFile getFileInfo(String id) {
        if (excelData.containsKey(id)) {
            return excelData.get(id);
        }
        return null;
    }


}

