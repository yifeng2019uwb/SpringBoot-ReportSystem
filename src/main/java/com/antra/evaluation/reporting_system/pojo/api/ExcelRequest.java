package com.yifeng.reporting_system.pojo.api;

import com.yifeng.reporting_system.pojo.report.ExcelDataSheet;
import org.springframework.lang.NonNull;

import java.util.List;

public class ExcelRequest {

    private String description;
    private String title;
    private List<ExcelDataSheet> sheets;


    public ExcelRequest(@NonNull  String title, @NonNull String description, @NonNull List<ExcelDataSheet> sheets) {
        this.title = title;
        this.description = description;
        this.sheets = sheets;
    }
    public ExcelRequest() {}

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public List<ExcelDataSheet> getSheets() {
        return sheets;
    }
}
