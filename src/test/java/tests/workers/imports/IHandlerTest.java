package tests.workers.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.constants.DateFormats;
import main.model.db.imports.Handler;
import main.model.dto.*;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.FileUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public interface IHandlerTest {

    DtoMapperGeneral mapper = new DtoMapperGeneral();

    Handler getHandler();

    String getReportPath();

    String getTestFileName();

    default String getFilePath(String fileName) {
        return getReportPath().concat(fileName);
    }

    default Date getFinishTime() {
        try {
            String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat(DateFormats.DATE_WITH_MONTH_NAME, Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(string);
        }catch (ParseException e){
            fail(String.format("Failed to parse expected finishTime: %s", e.getMessage()));
            return null;
        }
    }

    @BeforeMethod
    void tryParse();

    @Test
    default void validateTests() throws JsonProcessingException {
        List<TestDto> actualTests = getHandler().getTests();
        assertEquals(mapper.serialize(actualTests), FileUtils.getResourceFileAsString(getFilePath(getTestFileName())));
    }

    @Test
    default void validateTestSuite() throws JsonProcessingException {
        TestSuiteDto actualTestSuite = getHandler().getTestSuite();
        assertEquals(mapper.serialize(actualTestSuite), FileUtils.getResourceFileAsString(getFilePath("suite.json")));
    }

    @Test
    default void validateTestRun() throws JsonProcessingException {
        TestRunDto actualTestRun = getHandler().getTestRun();
        assertEquals(mapper.serialize(actualTestRun), FileUtils.getResourceFileAsString(getFilePath("testRun.json")));
    }

    @Test
    default void validateTestResults() throws JsonProcessingException {
        List<TestResultDto> actualTestResults = getHandler().getTestResults();
        assertEquals(mapper.serialize(actualTestResults), FileUtils.getResourceFileAsString(getFilePath("testResults.json")));
    }
}
