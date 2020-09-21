package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Override
    public synchronized InputStream getExcelBodyById(String id) throws FileNotFoundException {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()) {
            File file = new File(fileInfo.get().getDownloadLink());
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
                throw new FileNotFoundException();
            }
        }

        return null;
    }

    @Override
    public List<String> getFileNames(List<String> ids) throws FileNotFoundException {

        List<String> files = new ArrayList<>();
        for (String id : ids) {
            Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
            if (fileInfo.isPresent()) {
                files.add(fileInfo.get().getDownloadLink());
            } else {
                throw new FileNotFoundException();
            }
        }

        return files;
    }

}
