package com.yifeng.reporting_system.pojo.report;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


public class ExcelFile {

//    @NonNull
    private String fieldId;
    private String fileName;
    private LocalDateTime generatedTime;
    private long fileSize;
    private String downloadLink;

    public ExcelFile(){};

    public ExcelFile(String fileName, LocalDateTime generatedTime, long fileSize, String downloadLink ) {
        if (fileName.length() > 36) this.fieldId = fileName.substring(0, 36);
        else this.fieldId = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.generatedTime = generatedTime;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
