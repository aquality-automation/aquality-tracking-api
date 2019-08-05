package main.model.db.imports.SAXHandlers;

import main.model.db.imports.enums.ResultStatus;
import org.xml.sax.helpers.DefaultHandler;
import main.model.db.imports.enums.TestNameNodeType;
import main.model.dto.*;
import org.xml.sax.Attributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static main.model.db.imports.enums.ResultStatus.*;

public class TRXHandler extends DefaultHandler {
    private TestRunDto testRun = new TestRunDto();
    private List<TestResultDto> results = new ArrayList<>();
    private TestResultDto result = new TestResultDto();
    private List<TestDto> tests = new ArrayList<>();
    private TestDto test = new TestDto();
    private String currentElement = "";
    private String description = "";
    private boolean ddtResultWasOpened = false;
    private TestNameNodeType testNameNodeType;

    public TRXHandler(TestNameNodeType testNameNodeType){
        this.testNameNodeType = testNameNodeType;
        result.setFail_reason("$blank");
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        switch (qName) {
            case "TestRun":
                testRun.setAuthor(attributes.getValue("runUser"));
                break;
            case "Times":
                try {
                    testRun.setFinish_time(convertToDate(attributes.getValue("finish")));
                    testRun.setStart_time(convertToDate(attributes.getValue("start")));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "UnitTestResult":
                try {
                    if (Objects.equals(attributes.getValue("resultType"), "DataDrivenTest")) {
                        ddtResultWasOpened = true;
                    }
                    result.setInternalTestId(attributes.getValue("testId"));
                    result.setStart_date(convertToDate(attributes.getValue("startTime")));
                    if (attributes.getValue("endTime") != null) {
                        result.setFinish_date(convertToDate(attributes.getValue("endTime")));
                    } else {
                        result.setFinish_date(result.getStart_date());
                    }
                    result.setFinal_result_id(getStatus(attributes.getValue("outcome")).getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "UnitTest":
                test = new TestDto();
                if (testNameNodeType.equals(TestNameNodeType.testName)) {
                    test.setName(attributes.getValue("name"));
                }
                test.setInternalId(attributes.getValue("id"));
                break;
            case "TestMethod":
                if (testNameNodeType.equals(TestNameNodeType.className)) {
                    test.setName(attributes.getValue("className").split(",")[0]);
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentElement = "";
        if(qName.equals("UnitTestResult")){
            if(ddtResultWasOpened && result.getFinal_result_id() == null){
                ddtResultWasOpened = false;
            }else{
                if(result.getFail_reason().equals("$blank")) result.setFail_reason("");
                results.add(result);
                result = new TestResultDto();
                result.setFail_reason("$blank");
            }
        }else if(qName.equals("UnitTest")){
            tests.add(test);
        }else if(qName.equals("StdOut") && result.getInternalTestId() != null){
            TestDto resultTest = tests.stream().filter(x-> Objects.equals(x.getInternalId(), result.getInternalTestId())).findFirst().orElse(null);
            if(resultTest != null && !Objects.equals(description, "")) {
                description = description.replaceAll("(?m)^^(?!And|Given|  \\| |When|Then|-> error:).*(?:\\r?\\n)?", "");
                resultTest.setBody((resultTest.getBody() == null ? "" : resultTest.getBody()) + description);
            }
            description = "";
        }
        else if(qName.equals("Description") && result.getInternalTestId() != null){
            TestDto resultTest = tests.stream().filter(x-> Objects.equals(x.getInternalId(), result.getInternalTestId())).findFirst().orElse(null);
            if(resultTest != null && !Objects.equals(description, "")) {
                resultTest.setBody((resultTest.getBody() == null ? "" : resultTest.getBody()) + description);
            }
            description = "";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch,start,length);
        switch (currentElement) {
            case "Description":
                if (testNameNodeType.equals(TestNameNodeType.descriptionNode)) {
                    test.setName(value);
                } else {
                    String old = description;
                    if (old == null) old = "";
                    description = old.concat(value);
                }
                break;
            case "Message":
                String res = result.getFail_reason();
                if (res == null || res.equals("$blank")) res = "";
                result.setFail_reason(res.concat(value));
                break;
            case "StackTrace":
                String log = result.getLog();
                if (log == null) log = "";
                result.setLog(log.concat(value));
                break;
            case "StdOut":
                String old = description;
                if (old == null) old = "";
                description = old.concat(value);
                break;
        }
    }

    private Date convertToDate(String dateString) throws ParseException {
        String[] parts = dateString.split("\\.");
        String t = parts[0].replace('T', ' ');
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(t);
    }

    private ResultStatus getStatus(String status){
        switch (status) {
            case "Passed":
                return PASSED;
            case "Failed":
                return FAILED;
            case "Pending":
                return PENDING;
            default:
                return NOT_EXECUTED;
        }
    }

    public TestRunDto getTestRun(){
        return testRun;
    }

    public List<TestDto> getTests(){
        return tests;
    }

    public List<TestResultDto> getTestResults(){
        return results;
    }

    public TestSuiteDto getTestSuite(){ return new TestSuiteDto(); }
}
