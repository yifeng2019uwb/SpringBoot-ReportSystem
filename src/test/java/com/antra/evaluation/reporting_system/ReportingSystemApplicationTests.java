package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportingSystemApplicationTests {

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService reportService;

    @Autowired
    ExcelService excelService;

    private  ExcelData data;

    private MockRestServiceServer mockServer;

    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpData() {
        System.out.println( "setUpData ");
        data = new ExcelData();
        data.setTitle("Test book");
        data.setGeneratedTime(LocalDateTime.now());

        var sheets = new ArrayList<ExcelDataSheet>();
        var sheet1 = new ExcelDataSheet();
        sheet1.setTitle("First Sheet");

        var headersS1 = new ArrayList<ExcelDataHeader>();
        ExcelDataHeader header1 = new ExcelDataHeader();
        header1.setName("NameTest");
        //       header1.setWidth(10000);
        header1.setType(ExcelDataType.STRING);
        headersS1.add(header1);

        ExcelDataHeader header2 = new ExcelDataHeader();
        header2.setName("Age");
        //   header2.setWidth(10000);
        header2.setType(ExcelDataType.NUMBER);
        headersS1.add(header2);

        List<List<Object>> dataRows = new ArrayList<>();
        List<Object> row1 = new ArrayList<>();
        row1.add("Dawei");
        row1.add(12);
        List<Object> row2 = new ArrayList<>();
        row2.add("Dawei2");
        row2.add(23);
        dataRows.add(row1);
        dataRows.add(row2);

        sheet1.setDataRows(dataRows);
        sheet1.setHeaders(headersS1);
        sheets.add(sheet1);
        data.setSheets(sheets);

        var sheet2 = new ExcelDataSheet();
        sheet2.setTitle("second Sheet");
        sheet2.setDataRows(dataRows);
        sheet2.setHeaders(headersS1);
        sheets.add(sheet2);

        List<ExcelFile> allFiles = excelRepository.getFiles();
        System.out.println( allFiles.size() + " files in repository now");
        if (allFiles.size() > 0 ) {
            for (ExcelFile f : allFiles) {
                System.out.println(f.getField());
            }
        }
    }

    @Test
    @Order(1)
    public void testExcelGegeration() {
        File file = null;
        try {
            file = reportService.generateExcelReport(data);
            // for get test
            excelRepository.saveFile(reportService.createExcelFile(file, data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(file != null);
    }

    @Test
    @Order(2)
    public void testGetFile() throws IOException {

        assertTrue(excelRepository.findFile("Test book") );
    }

    @Test
    @Order(3)
    public void testDeleteFile() throws IOException {
        try {
            assertTrue(excelRepository.deleteFile("Test book"));
        }
        finally {
            excelRepository.removeRecord("Test book");
        }

    }

    @Test
//    @Disabled
    @Order(4)
    public void testSaveBatchofFiles() throws IOException {
        // This is not necessary to test ?
        File file = null;
        try {
            file = reportService.generateExcelReport(data);
            // for get test
            data.setTitle(("Test Book 10"));
            excelRepository.saveFile(reportService.createExcelFile(file, data));

            data.setTitle(("Test Book 20"));
            file = reportService.generateExcelReport(data);
            excelRepository.saveFile(reportService.createExcelFile(file, data));

            data.setTitle(("Test Book 30"));
            file = reportService.generateExcelReport(data);
            excelRepository.saveFile(reportService.createExcelFile(file, data));

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(excelRepository.getFiles().size());
        assertTrue(excelRepository.getFiles().size() == 3 );

    }

    @Test
    @Disabled
    @Order(5)
    public void testDownloadZipFile() throws IOException {

    }


}
