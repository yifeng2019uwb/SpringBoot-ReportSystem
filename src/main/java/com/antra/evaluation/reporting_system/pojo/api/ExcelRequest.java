package com.antra.evaluation.reporting_system.pojo.api;


import java.util.List;

import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelRequest {

    private String filename;
    private String description;
    private List<ExcelDataHeader> headers;
    private List<List<String>> data;
    private String submitter;

}
