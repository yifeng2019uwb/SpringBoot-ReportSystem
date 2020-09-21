package com.antra.evaluation.reporting_system.pojo.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
public class MultiSheetExcelRequest extends ExcelRequest {

    @NonNull
    private String splitBy;

}
