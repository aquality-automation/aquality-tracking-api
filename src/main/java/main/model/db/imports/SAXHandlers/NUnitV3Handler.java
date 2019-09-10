package main.model.db.imports.SAXHandlers;

import main.model.db.imports.ResultStatus;
import main.exceptions.AqualityException;
import main.model.db.imports.Handler;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static main.model.db.imports.ResultStatus.*;
import static main.model.db.imports.TestNameNodeType.*;

public class NUnitV3Handler extends Handler {
    private TestSuiteDto testSuite = new TestSuiteDto();
    private TestRunDto testRun = new TestRunDto();
    private List<TestResultDto> results = new ArrayList<>();
    private TestResultDto result = new TestResultDto();
    private List<TestDto> tests = new ArrayList<>();
    private TestDto test = new TestDto();
    private String currentElement = "";
    private String currentFixture = "";
    private Boolean isFailureStarted = false;
    private Boolean isAssertionStarted = false;
    private Boolean isReasonStarted = false;
    private Boolean isTestCaseStarted = false;
    private Integer assertionNumber = 0;
    private TestNameNodeType testNameNodeType;

    public NUnitV3Handler(TestNameNodeType testNameNodeType) throws AqualityException {
        super();
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
        currentElement = qName;
        switch (qName) {
            case "test-run":
                try {
                    testRun.setStart_time(convertToDate(attributes.getValue("start-time")));
                    testRun.setFinish_time(convertToDate(attributes.getValue("end-time")));
                } catch (ParseException e) {
                    throw new SAXException("Can not parse start or end time in report");
                }
                break;
            case "environment":
                testRun.setExecution_environment(attributes.getValue("machine-name"));
                testRun.setAuthor(String.format("%s\\%s", attributes.getValue("user-domain"), attributes.getValue("user")));
                break;
            case "test-suite":
                if(attributes.getValue("type").equals("TestFixture") && testNameNodeType == featureNameTestName) {
                    currentFixture = attributes.getValue("name");
                }
                break;
            case "test-case":
                isTestCaseStarted = true;
                setTestName(attributes);
                try {
                    result.setStart_date(convertToDate(attributes.getValue("start-time")));
                    result.setFinish_date(convertToDate(attributes.getValue("end-time")));
                } catch (ParseException e) {
                    throw new SAXException("Can not parse start or end test time in report");
                }
                result.setFinal_result_id(getStatus(attributes.getValue("result")).getValue());
                break;
            case "failure":
                isFailureStarted = true;
                break;
            case "assertion":
                isAssertionStarted = true;
                break;
            case "reason":
                isReasonStarted = true;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentElement = "";
        switch (qName) {
            case "test-case":
                test.setInternalId(test.getName());
                tests.add(test);
                test = new TestDto();
                result.setInternalTestId(test.getName());
                if(result.getFail_reason() != null && result.getFail_reason().equals("$blank")){
                    result.setFail_reason("");
                }
                if(result.getFinal_result_id() != PASSED.getValue() && result.getFail_reason().equals("")) {
                    result.setFail_reason("Failed without any message. Please see logs on result page.");
                }
                results.add(result);
                result = new TestResultDto();
                result.setFail_reason("$blank");
                isTestCaseStarted = false;
                break;
            case "failure":
                isFailureStarted = false;
                break;
            case "assertion":
                isAssertionStarted = false;
                assertionNumber++;
                break;
            case "reason":
                isReasonStarted = false;
                break;
            case "assertions":
                assertionNumber = 0;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch,start,length);
        if(isFailureStarted && isTestCaseStarted) {
            if (currentElement.equals("message")) {
                String res = result.getFail_reason();
                if (res == null || res.equals("$blank")) res = "";
                result.setFail_reason(res.concat(value));
            } else if (currentElement.equals("stack-trace")) {
                String log = result.getLog();
                if (log == null) log = "";
                result.setLog(log.concat(value));
            }
        } else if(isAssertionStarted && isTestCaseStarted) {
            if (currentElement.equals("message")) {
                String res = result.getFail_reason();
                if (res == null || res.equals("$blank") || assertionNumber == 0){
                    res = "Assertion:\r\n";
                }
                if(assertionNumber > 0) {
                    value = String.format("\r\nAssertion [%d] \r\n %s", assertionNumber + 1, value);
                }
                result.setFail_reason(res.concat(value));
            } else if (currentElement.equals("stack-trace")) {
                String log = result.getLog();
                if (log == null) {
                    log = "";
                }
                if(assertionNumber > 0) {
                    value = String.format("\r\nAssertion [%d] \r\n %s", assertionNumber + 1, value);
                }
                result.setLog(log.concat(value));
            }
        } else if(isReasonStarted && isTestCaseStarted) {
            if (currentElement.equals("message")) {
                String res = result.getFail_reason();
                if (res == null || res.equals("$blank")){
                    res = "";
                }
                result.setFail_reason(res.concat(value));
            }
        }
    }

    private void setTestName(Attributes attributes) throws SAXException {
        switch (testNameNodeType) {
            case featureNameTestName:
                test.setName(String.format("%s: %s", currentFixture, attributes.getValue("name")) );
                break;
            case className:
                test.setName(attributes.getValue("fullname"));
                break;
            default:
                throw new SAXException("testNameNodeType is not correct for NUnitV3 parser.");
        }
    }

    private Date convertToDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateString);
    }

    private ResultStatus getStatus(String status){
        switch (status) {
            case "Passed":
                return PASSED;
            case "Warning":
            case "Failed":
                return FAILED;
            case "Skipped":
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
