package com.antra.evaluation.reporting_system.pojo.report;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExcelData {
    private String title;
    private LocalDateTime generatedTime;
    private List<ExcelDataSheet> sheets;

}
