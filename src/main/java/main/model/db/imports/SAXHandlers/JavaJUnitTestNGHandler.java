package main.model.db.imports.SAXHandlers;

import main.exceptions.AqualityException;
import main.model.db.imports.Handler;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static main.model.db.imports.ResultStatus.*;

public class JavaJUnitTestNGHandler extends Handler {
    private TestSuiteDto testSuite = new TestSuiteDto();
    private List<TestResultDto> results = new ArrayList<>();
    private TestResultDto result = new TestResultDto();
    private List<TestDto> tests = new ArrayList<>();
    private TestDto test = new TestDto();
    private String currentElement = "";
    private Calendar calendar = Calendar.getInstance();
    private Date currentTimeSlot;
    private TestNameNodeType testNameNodeType;

    public JavaJUnitTestNGHandler(TestNameNodeType testNameNodeType, Date finishTime) throws AqualityException {
        super();
        this.testNameNodeType = testNameNodeType;
        result.setFail_reason("$blank");
        testRun.setFinish_time(finishTime);
    }

    @Override
    public void startDocument(){
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes){
        currentElement = qName;
        if(qName.equals("testsuite")){
            calendar.setTime(testRun.getFinish_time());
            long longTime = Math.round(Double.parseDouble(attributes.getValue("time").replaceAll(",","").split("\\.")[0]));
            calendar.add(Calendar.SECOND, -(int) longTime);
            testRun.setStart_time(calendar.getTime());
            currentTimeSlot = testRun.getFinish_time();
        } else if(qName.equals("property") && attributes.getValue("name").equals("suite")){
            testSuite.setName(attributes.getValue("value"));
        } else if(qName.equals("testcase")) {
            if(testNameNodeType.equals(TestNameNodeType.className)){
                test.setName(attributes.getValue("classname"));
            }else if(testNameNodeType.equals(TestNameNodeType.testName)){
                test.setName(attributes.getValue("name"));
            }
            
            result.setFinal_result_id(PASSED.getValue());
            result.setFinish_date(currentTimeSlot);
            calendar.setTime(currentTimeSlot);
            calendar.add(Calendar.SECOND, -Integer.parseInt(attributes.getValue("time").replaceAll(",","").split("\\.")[0]));
            currentTimeSlot = calendar.getTime();
            result.setStart_date(currentTimeSlot);

        } else if(qName.equals("failure")){
            
            result.setFinal_result_id(FAILED.getValue());
        } else if(qName.equals("skipped")){
            
            result.setFinal_result_id(PASSED.getValue());
            result.setFail_reason(attributes.getValue("message"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName){
        currentElement = "";
        if (qName.equals("testcase")) {
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
    public void characters(char[] ch, int start, int length){
        String value = new String(ch,start,length);
        if(currentElement.equals("failure")){
            String res = result.getFail_reason();
            if (res == null || res.equals("$blank")) res = "";
            result.setFail_reason("");
            result.setFail_reason(res.concat(value));
        }
        else if(currentElement.equals("system-out")){
            String log = result.getLog();
            if (log  == null) log = "";
            result.setLog(log.concat(value));
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
