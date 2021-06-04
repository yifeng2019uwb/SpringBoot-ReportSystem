package com.yifeng.reporting_system.pojo.api;

import com.yifeng.reporting_system.pojo.report.ExcelDataSheet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MultiSheetExcelRequest extends ExcelRequest {

    @NonNull
    private String splitBy;

    public MultiSheetExcelRequest(@NonNull String title, @NonNull String description, @NonNull List<ExcelDataSheet> sheets, @NonNull String splitBy) {
        super(title, description, sheets);
        this.splitBy = splitBy;
    }

}
