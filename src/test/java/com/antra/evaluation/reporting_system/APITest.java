package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.Exception.ApplicationException;
import com.antra.evaluation.reporting_system.Exception.BadRequestException;
import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import com.antra.evaluation.reporting_system.service.ExcelServiceImpl;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


public class APITest {


    @InjectMocks
    private ExcelService excelService = new ExcelServiceImpl();

    @Mock
    ExcelRepository excelRepository;

    @Mock
    ExcelGenerationService excelGenerationService;

    @Mock
    ExcelData excelData;

    ExcelService spyExcelService;

    String data = "{\n" +
            "  \"title\": \"Test book\",\n" +
            "  \"description\": \"\",\n" +
            "  \"sheets\": [{\n" +
            "      \"title\": \"First Sheet\",\n" +
            "      \"headers\": [{\n" +
            "          \"name\": \"NameTest\",\n" +
            "          \"width\": \"20\",\n" +
            "          \"type\": \"STRING\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Age\",\n" +
            "          \"type\": \"NUMBER\",\n" +
            "          \"width\": \"15\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"dataRows\": [\n" +
            "        [\"Dawei\", 12],\n" +
            "         [\"Dawei2\", 22]\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"title\": \"Second Sheet\",\n" +
            "      \"headers\": [{\n" +
            "          \"name\": \"NameTest\",\n" +
            "          \"width\": \"20\",\n" +
            "          \"type\": \"STRING\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Age\",\n" +
            "          \"type\": \"NUMBER\",\n" +
            "          \"width\": \"15\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"dataRows\": [\n" +
            "        [\"Dawei\", 12],\n" +
            "         [\"Dawei2\", 22]\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
        spyExcelService = Mockito.spy(excelService);
    }

    @Test
    public void testExcelGeneration() throws IOException {
        Mockito.when(excelService.generateExcelFile(any())).thenReturn(new File("temp.xlsx"));
        given().accept("application/json")
                .contentType(ContentType.JSON)
                .body(data)
                .post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("fileId", Matchers.notNullValue());
    }

    @Test
    public void testExcelGenerationBadRequest() throws IOException{
        Mockito.when(excelService.generateExcelFile(any()))
                .thenThrow(new BadRequestException("Bad Data"));
        given().accept("application/json").contentType(ContentType.JSON)
                .body(data)
                .post("/excel").peek()
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    public void testListFileWithEmptyBucket()  {
        given().accept("application/json")
                .get("/excel").peek().then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void testListFiles()  {
        List<String> fileList = new ArrayList<>();
        fileList.add("test1");
        fileList.add("test2");
        Mockito.when(excelService.getAllFileList()).thenReturn(fileList);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200);

    }

    @Test
    public void testDeleteFile() throws FileNotFoundException {
        Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();
        excelData.put("temp", new ExcelFile());
        Mockito.when(excelService.deleteFile(any())).thenReturn("temp");
        given().accept("application/json").contentType(ContentType.JSON)
                .delete("/excel/123abc").peek().
                then().assertThat()
                .statusCode(200);

    }

    @Test
    public void testFileDeleteFileNotFound(){
        Mockito.when(excelService.deleteFile(any()))
                .thenThrow(new ApplicationException("not found"));
        given().accept("application/json").delete("/excel/123ABC")
                .peek().then().assertThat()
                .statusCode(400);
    }


    @Test
    public void testFileDownload() throws FileNotFoundException {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(200);
    }






}