package main.model.db.imports.SAXHandlers;

import main.model.db.imports.enums.ResultStatus;
import main.exceptions.RPException;
import main.model.db.imports.Handler;
import main.model.dto.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static main.model.db.imports.enums.ResultStatus.*;

public class NUnitV2Handler extends Handler {


    private TestSuiteDto testSuite = new TestSuiteDto();
    private TestRunDto testRun = new TestRunDto();
    private List<TestResultDto> results = new ArrayList<TestResultDto>();
    private TestResultDto result = new TestResultDto();
    private List<TestDto> tests = new ArrayList<TestDto>();
    private TestDto test = new TestDto();
    private String currentElement = "";
    private Calendar calendar = Calendar.getInstance();
    private Date currentTimeSlot;
    private Boolean isTestFailed = false;
    private Boolean isTestCaseClose = false;
    private String strTime;
    private int milliTime;

    public NUnitV2Handler() throws RPException {
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = qName;
        if(qName.equals("test-results")){
            try {
                testRun.setFinish_time(convertToDate(attributes.getValue("date"), attributes.getValue("time")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if (qName.equals("environment")) {
            testRun.setExecution_environment(attributes.getValue("machine-name"));
            testRun.setAuthor(String.format("%s\\%s", attributes.getValue("user-domain"), attributes.getValue("user")));

        }
        else if (qName.equals("test-suite") && attributes.getValue("type").equals("TestSuite") && strTime == null) {

            calendar.setTime(testRun.getFinish_time());
            strTime = attributes.getValue("time");
            milliTime = Integer.parseInt(strTime.replaceAll("\\.",""));
            calendar.add(Calendar.MILLISECOND, -milliTime);
            testRun.setStart_time(calendar.getTime());
            currentTimeSlot = testRun.getStart_time();
        } else if(qName.equals("test-suite") && attributes.getValue("type").equals("TestSuite")
                && strTime.equals(attributes.getValue("time"))){
            testSuite.setName(attributes.getValue("name"));
        } else if(qName.equals("test-case")) {
            isTestCaseClose = false;
            test.setName(attributes.getValue("name"));
            result.setStart_date(currentTimeSlot);
            calendar.setTime(currentTimeSlot);
            milliTime = Integer.parseInt(attributes.getValue("time").replaceAll("\\.",""));
            calendar.add(Calendar.MILLISECOND, milliTime);
            currentTimeSlot = calendar.getTime();
            result.setFinish_date(currentTimeSlot);

            if(attributes.getValue("success").equals("False")){
                isTestFailed = true;
            }


            result.setFinal_result_id(getStatus(attributes.getValue("result")).getValue());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentElement = "";
        if (qName.equals("test-case") || (!isTestCaseClose && qName.equals(""))) {
            isTestFailed = false;
            isTestCaseClose = true;
            test.setInternalId(test.getName());
            tests.add(test);
            test = new TestDto();
            result.setInternalTestId(test.getName());
            if(result.getFail_reason() != null && result.getFail_reason().equals("$blank")) result.setFail_reason("");
            results.add(result);
            result = new TestResultDto();
            result.setFail_reason("$blank");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch,start,length);
        if (isTestFailed) {
            if (currentElement.equals("message")) {
                String res = result.getFail_reason();
                if (res == null || res.equals("$blank")) res = "";
                result.setFail_reason("");
                result.setFail_reason(res.concat(value));
            } else if (currentElement.equals("stack-trace")) {
                String log = result.getLog();
                if (log == null) log = "";
                result.setLog(log.concat(value));
            }
        }
    }


    private Date convertToDate(String dateString, String timeString) throws ParseException {
        String date;
        date = String.format("%1$s %2$s", dateString, timeString);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(date);
    }

    private ResultStatus getStatus(String status){
        switch (status) {
            case "Success":
                return PASSED;
            case "Error":
            case "Failure":
                return FAILED;
            default:
                return NOT_EXECUTED;
        }
    }

    public TestSuiteDto getTestSuite(){
        return testSuite;
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
}
