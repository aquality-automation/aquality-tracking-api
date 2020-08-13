package main.model.db.imports.SAXHandlers;

import main.exceptions.AqualityException;
import main.model.db.imports.Handler;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
import org.xml.sax.Attributes;

import java.util.*;
import java.util.function.Function;

import static main.model.db.imports.ResultStatus.*;

public class MavenSurefireSaxHandler extends Handler {
    private TestSuiteDto testSuite = new TestSuiteDto();
    private List<TestResultDto> results = new ArrayList<>();
    private TestResultDto result;
    private List<TestDto> tests = new ArrayList<>();
    private TestDto test = new TestDto();
    private String currentElement = "";
    private Calendar calendar = Calendar.getInstance();
    private Date currentTimeSlot;
    private TestNameNodeType testNameNodeType;

    private static final String BLANK_RESULT = "$blank";

    public MavenSurefireSaxHandler(TestNameNodeType testNameNodeType, Date finishTime) throws AqualityException {
        super();
        this.testNameNodeType = testNameNodeType;
        testRun.setFinish_time(finishTime);
        initEmptyResult();
    }

    @Override
    public void startDocument() {
        // there are no needs to do something on this stages
    }

    @Override
    public void endDocument() {
        // there are no needs to do something on this stages
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        if (qName.equals("testsuite")) {
            calendar.setTime(testRun.getFinish_time());
            long longTime = Math.round(parseTime(attributes, Double::parseDouble));
            calendar.add(Calendar.SECOND, -(int) longTime);
            testRun.setStart_time(calendar.getTime());
            currentTimeSlot = testRun.getFinish_time();
        } else if (qName.equals("property") && attributes.getValue("name").equals("suite")) {
            testSuite.setName(attributes.getValue("value"));
        } else if (qName.equals("testcase")) {
            String name = getTestName(testNameNodeType, attributes);
            test.setName(name);
            result.setFinal_result_id(PASSED.getValue());
            result.setFinish_date(currentTimeSlot);
            calendar.setTime(currentTimeSlot);
            calendar.add(Calendar.SECOND, -parseTime(attributes, Integer::parseInt));
            currentTimeSlot = calendar.getTime();
            result.setStart_date(currentTimeSlot);
        } else if (isError(qName)) {
            result.setFinal_result_id(FAILED.getValue());
        } else if (qName.equals("skipped")) {
            result.setFinal_result_id(NOT_EXECUTED.getValue());
            result.setFail_reason(attributes.getValue("message"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("testcase")) {
            test.setInternalId(test.getName());
            tests.add(test);
            test = new TestDto();
            result.setInternalTestId(test.getName());

            String failReason = result.getFail_reason();
            if (failReason != null && failReason.equals(BLANK_RESULT)) {
                result.setFail_reason("");
            }
            results.add(result);

            currentElement = "";
            initEmptyResult();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length);
        if (isError(currentElement)) {
            String res = result.getFail_reason();
            res = (res == null || res.equals(BLANK_RESULT)) ? "" : res;
            result.setFail_reason(res.concat(value));
        } else if (currentElement.equals("system-out")) {
            String log = result.getLog();
            if (log == null) log = "";
            result.setLog(log.concat(value));
        }
    }

    private void initEmptyResult() {
        result = new TestResultDto();
        result.setFail_reason(BLANK_RESULT);
    }

    private String getTestName(TestNameNodeType type, Attributes attributes) {
        switch (type) {
            case className:
                return attributes.getValue("classname");
            case testName:
                return attributes.getValue("name");
            default:
                throw new IllegalArgumentException("There is no implementation for getting test name by type " + type);
        }
    }

    private <T extends Number> T parseTime(Attributes attributes, Function<String, T> transform) {
        String value = attributes.getValue("time")
                .replace(",", "")
                .split("\\.")[0];
        return transform.apply(value);
    }

    private boolean isError(String qName) {
        List<String> errorTags = Arrays.asList("failure", "error");
        return errorTags.contains(qName.toLowerCase());
    }

    public TestSuiteDto getTestSuite() {
        return testSuite;
    }

    public TestRunDto getTestRun() {
        return testRun;
    }

    public List<TestDto> getTests() {
        return tests;
    }

    public List<TestResultDto> getTestResults() {
        return results;
    }
}
