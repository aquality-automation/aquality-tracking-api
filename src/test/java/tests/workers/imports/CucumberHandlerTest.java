package tests.workers.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.model.db.imports.ImportHandlers.Cucumber;
import main.model.dto.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.FileUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class CucumberHandlerTest {
    private Cucumber cucumber;
    private DtoMapperGeneral mapper = new DtoMapperGeneral();

    @BeforeMethod
    public void tryParse(){
        try {
            String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            cucumber = new Cucumber(FileUtils.getResourceFile("reports/cucumber/cucumber.json"), format.parse(string));
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }

    @Test
    public void validateTests() throws JsonProcessingException {
        List<TestDto> actualTests = cucumber.getTests();
        assertEquals(mapper.serialize(actualTests), FileUtils.getResourceFileAsString("reports/cucumber/tests.json"));
    }

    @Test
    public void validateTestSuite() throws JsonProcessingException {
        TestSuiteDto actualTestSuite = cucumber.getTestSuite();
        assertEquals(mapper.serialize(actualTestSuite), FileUtils.getResourceFileAsString("reports/cucumber/suite.json"));
    }

    @Test
    public void validateTestRun() throws JsonProcessingException {
        TestRunDto actualTestRun = cucumber.getTestRun();
        assertEquals(mapper.serialize(actualTestRun), FileUtils.getResourceFileAsString("reports/cucumber/testRun.json"));
    }

    @Test
    public void validateTestResults() throws JsonProcessingException {
        List<TestResultDto> actualTestResults = cucumber.getTestResults();
        assertEquals(mapper.serialize(actualTestResults), FileUtils.getResourceFileAsString("reports/cucumber/testResults.json"));
    }
}
