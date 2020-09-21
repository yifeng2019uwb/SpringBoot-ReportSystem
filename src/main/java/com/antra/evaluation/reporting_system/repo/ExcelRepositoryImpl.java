package com.antra.evaluation.reporting_system.repo;


import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
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

        excelData.put(file.getField(), file);
        return file;
    }

    @Override
    public boolean deleteFile(String id) {
        ExcelFile efile = excelData.get(id);
        if (efile == null) {
            return false;
        }
        File file = new File(efile.getDownloadLink());

//        String path = efile.getDownloadLink();
//        excelData.remove(path.substring(0, path.length() - 5));

        return file.delete();

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
    public void removeRecord(String fileName) {
        excelData.remove(fileName);
    }


}

