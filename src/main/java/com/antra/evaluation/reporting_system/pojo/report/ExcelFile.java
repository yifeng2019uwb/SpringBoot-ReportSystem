package com.antra.evaluation.reporting_system.pojo.report;

import lombok.*;

import java.time.LocalDateTime;


//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Getter
@Setter
public class ExcelFile {

//    @NonNull
    private String field;
    private LocalDateTime generatedTime;
    private long filesize;
    private String downloadLink;

    // This is for me to test the all parameters in the class
    @Override
    public String toString() {
        return "ExcelFile{" +
                "field='" + field + '\'' +
                ", generatedTime=" + generatedTime +
                ", filesize=" + filesize +
                ", downloadLink='" + downloadLink + '\'' +
                '}';
    }
}
