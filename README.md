## Environment
In order to compile and run this assessment correctly, below environments are required:
* Java JDK 11+
* Maven
* Also you may need software to view the generated Excel files like Microsoft Office, LibreOffice, WPS, Google Doc etc.

## Objectives
 * Design and implementation of REST webservices/endpoints.


 ## Install
 ### Run the below command to compile and setup:
 * _`mvn install`_
  * or _`mvn install -Dmaven.test.skip=true`_ if the test fails during the building.
 ### Run the below command to run the tests:
 * _`mvn test`_


 ## Project Explaination
 This project is simulating REST apis for the generation of Excel Files from user customizable data.
 The format of incoming data is not pre-defined, which means the user should pass data for each row and column. APIs are including: 
  * Generate Excel Files and store the files on the server for further downloading.
  * Excel Files could have 2 flavors: single sheet and multi-sheet.
  * User data should indicate which header to be used to split the data into multiple sheet.
  * An API for list and search excel files.
  * Delete file by id.
  * Add batch generation api that can accept multiple data as the same time and generate multiple excel at once.
  * Add batch downloading api that allow user to download multiple files in one request. Downloaded File should be in zip format.


## See [ExcelReportSystem.pdf](ExcelReportSystem.pdf) for more details.

 ### Valid incoming data
 * For single sheet api - _fields may vary depending on your design._
 ```json
 {"headers":["Name","Age"], "data":[["Teresa","5"],["Daniel","1"]]}
 ```
 * For auto split sheet api _- fields may vary depending on your design._
```json
{"headers":["Name","Age","Class"], "data":[["Teresa","5","A"],["Daniel","1","B"]], "splitBy":"Class"}
```
 * See _ExcelGenerationController_ for Detailed explaination of each API.

## Data storage
 * Using ConcurrentHashMap to simulate data storage in  local storage(current path).

## Documents
* [ExcelReportSystem.pdf](ExcelReportSystem.pdf)
* http://localhost:8080/swagger-ui.html

## Test
Use [mathData.json](mathData.json) and [mathData-withSplitBy.json](mathData-withSplitBy.json) as the payload when testing with Postman or Junit.
[mathData.xlsx](mathData.xlsx) and [mathData-withSplitBy.xlsx](mathData-withSplitBy.xlsx) are two example excel files for the test data.

