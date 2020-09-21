package com.antra.evaluation.reporting_system.pojo.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelDataHeader {
    private String name;
    private ExcelDataType type;
    private int width;

}
