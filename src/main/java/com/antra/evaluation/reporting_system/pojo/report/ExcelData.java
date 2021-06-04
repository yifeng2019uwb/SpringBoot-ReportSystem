package com.yifeng.reporting_system.pojo.report;

import com.yifeng.reporting_system.pojo.api.ExcelRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ExcelData {
    private String title;
    private LocalDateTime generatedTime;
    private List<ExcelDataSheet> sheets;

    public ExcelData(ExcelRequest request) {
        this.title = request.getTitle();
        this.generatedTime = LocalDateTime.now();
        this.sheets = request.getSheets();
    }

    public ExcelData() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public List<ExcelDataSheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<ExcelDataSheet> sheets) {
        this.sheets = sheets;
    }
}
