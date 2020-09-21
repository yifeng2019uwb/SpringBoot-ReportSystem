package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;

public class APITest {

    @Mock
    private ExcelRepository excelRepository;

    @Mock
    private ExcelService excelService;

    @Mock
    private ExcelGenerationService excelGenerationService;

    @Mock
    List<String> mockList;

//    @Mock
//    ExcelData data;


    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService,excelGenerationService,excelRepository));

    }

    @Test
    public void testFileDownload() throws FileNotFoundException {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testListFiles() throws FileNotFoundException {
        // Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testExcelGeneration() throws IOException, ParseException {
//        File mockFile = new File("temp.xlsx");
//        System.out.println("Mock file path" + mockFile.getPath());

        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"filename\": \"mockTest\",\"description\": \"test\",\"headers\": [{\"name\": \"Name\",\"type\": \"STRING\",\"width\": \"15\"},{\"name\": \"Age\",\"type\": \"NUMBER\",\"width\": \"15\"}],\"data\": [[\"A\", 10],[\"B\", 20]],\"submitter\": \"Ms.\"}")
                .post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void testDeleteFile() throws FileNotFoundException {
        Mockito.when(excelRepository.deleteFile(anyString())).thenReturn(true);
        given().accept("application/json").contentType(ContentType.JSON)
                .body(anyString())
                .delete("/excel/123abc").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void testExcelGenerationMultiSheet() throws FileNotFoundException {
        // Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"filename\": \"Test\",\"description\": \"test\",\"headers\": [{\"name\": \"Name\",\"type\": " +
                        "\"STRING\",\"width\": \"15\"},{\"name\": \"Age\",\"type\": \"NUMBER\",\"width\": \"15\"}]" +
                        ",\"data\": [[\"A\", 10],[\"B\", 20]],\"submitter\": \"Ms.\", \"splitBy\":\"Name\"}")
                .post("/excel/auto").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }


    @Test
    public void testBatchGeneration() throws FileNotFoundException {
        // Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{{\"filename\": \"Test\",\"description\": \"test\",\"headers\": " +
                        "[{\"name\": \"Name\",\"type\": \"STRING\",\"width\": \"15\"},{\"name\": \"Age\"" +
                        ",\"type\": \"NUMBER\",\"width\": \"15\"}],\"data\": [[\"A\", 10],[\"B\", 20]]" +
                        ",\"submitter\": \"Ms.\", \"splitBy\":\"Name\"}}}")
                .post("/excel/batch").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void testDownloadZip() throws FileNotFoundException {
        // test1.xlsx and test2.xlsx are already in fold
        Mockito.when(excelService.getFileNames(anyList())).thenReturn(asList("test1.xlsx","test2.xlsx"));
        given().accept("application/json")
                .get("/excel/downloadZip?ids=test1,test2").peek().
                then().assertThat()
                .statusCode(200)
                .body( Matchers.notNullValue());
    }

}