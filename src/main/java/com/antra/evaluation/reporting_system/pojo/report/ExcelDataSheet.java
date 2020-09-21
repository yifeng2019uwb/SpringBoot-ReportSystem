package com.antra.evaluation.reporting_system.pojo.report;

import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.lang.NullPointerException;

@Getter
@Setter
public class ExcelDataSheet extends NullPointerException{
    private String title;
    private List<ExcelDataHeader> headers;
    private List<List<Object>> dataRows;

    public void addDataRow(List<String> dataRow) throws ParseException {
        List<Object> newRow = new ArrayList<>();

        for (int i = 0; i < dataRow.size(); i++) {
            switch (headers.get(i).getType()){
                case NUMBER:
                    Object n = Double.parseDouble(dataRow.get(i));
                    newRow.add(n);
                    break;
                case DATE:
                    // exception handler??
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    newRow.add(sdf.parse(dataRow.get(i)));
                    break;
                default:
                    Object s = new String(dataRow.get(i));
                    newRow.add(s);
                    break;
            }
        }
        if (this.dataRows == null) this.dataRows = new ArrayList<>();
        this.dataRows.add(newRow);

    }

}
