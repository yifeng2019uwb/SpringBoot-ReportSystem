package com.antra.evaluation.reporting_system.pojo.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExcelDataType {
    @JsonProperty
    STRING,
    @JsonProperty
    NUMBER,
    @JsonProperty
    DATE
}
