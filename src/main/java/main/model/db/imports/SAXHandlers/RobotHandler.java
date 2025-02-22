package main.model.db.imports.SAXHandlers;

import main.constants.DateFormats;
import main.exceptions.AqualityException;
import main.model.db.imports.Handler;
import main.model.db.imports.ResultStatus;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
import org.xml.sax.Attributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static main.model.db.imports.ResultStatus.*;

public class RobotHandler extends Handler {
    private TestSuiteDto testSuite = new TestSuiteDto();
    private List<TestResultDto> results = new ArrayList<>();
    private TestResultDto result = new TestResultDto();
    private List<TestDto> tests = new ArrayList<>();
    private TestDto test = new TestDto();
    private boolean testStarted = false;
    private boolean kwStarted = false;
    private String currentKWName = "";
    private String currentLogMessage ="";
    private String currentElement = "";
    private boolean errorMessage = false;

    public RobotHandler() throws AqualityException {
        super();
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
        if (qName.equals("robot")){
            try {
                testRun.setStart_time(convertToDate(attributes.getValue("generated")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (qName.equals("suite") && attributes.getValue("id") != null && attributes.getValue("id").equals("s1")){
            testSuite.setName(attributes.getValue("name"));
        } else if(qName.equals("test")){
            testStarted = true;
            test.setName(attributes.getValue("name"));
        } else if(qName.equals("kw")){
            kwStarted = true;
            currentKWName = attributes.getValue("name");
        } else if(qName.equals("status") && testStarted && !kwStarted){
            try {
                result.setFinal_result_id(getStatus(attributes.getValue("status")).getValue());
                result.setStart_date(convertToDate(attributes.getValue("starttime")));
                result.setFinish_date(convertToDate(attributes.getValue("endtime")));
                testRun.setFinish_time(convertToDate(attributes.getValue("endtime")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(qName.equals("msg")){
            testStarted = true;
            errorMessage = attributes.getValue("level").equals("FAIL");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentElement = "";
        switch (qName) {
            case "test":
                result.setLog(currentLogMessage);
                currentLogMessage = "";
                results.add(result);
                tests.add(test);
                test = new TestDto();
                result = new TestResultDto();
                result.setFail_reason("$blank");
                testStarted = false;
                break;
            case "kw":
                kwStarted = false;
                currentKWName = "";
                break;
            case "msg":
                errorMessage = false;
                break;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if(currentElement.equals("doc") && testStarted && !kwStarted){
            String body = new String(ch,start,length);
            if(test.getBody() != null){
                body = test.getBody() +"\r\n"+ body;
            }
            test.setBody(body);
        } else if(currentElement.equals("status") && testStarted && !kwStarted && length > 0){
            if (result.getFail_reason() == null || result.getFail_reason().equals("$blank")) result.setFail_reason("");
            result.setFail_reason(new String(ch,start,length));
            currentLogMessage += new String(ch,start,length);
        }
        else if(currentElement.equals("msg") && errorMessage && testStarted && kwStarted && length > 0){
            currentLogMessage +=  "\r\n" + currentKWName + " | " + new String(ch,start,length);
        }
    }

    private Date convertToDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat(DateFormats.COMPACT_12H_DATETIME);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateString);
    }

    private ResultStatus getStatus(String status) {
        switch (status) {
            case "PASS":
                return PASSED;
            case "FAIL":
                return FAILED;
            default:
                return NOT_EXECUTED;
        }
    }

    public TestRunDto getTestRun(){
        return testRun;
    }

    public TestSuiteDto getTestSuite(){
        return testSuite;
    }

    public List<TestDto> getTests(){
        return tests;
    }

    public List<TestResultDto> getTestResults(){
        return results;
    }

}
