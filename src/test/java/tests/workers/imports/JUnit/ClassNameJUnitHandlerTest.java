package tests.workers.imports.JUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.model.db.imports.ImportHandlers.JavaJUnitTestNG;
import main.model.db.imports.ImportHandlers.TRX;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.FileUtils;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ClassNameJUnitHandlerTest {
    private JavaJUnitTestNG javaJUnitTestNGHandler;
    private DtoMapperGeneral mapper = new DtoMapperGeneral();

    @BeforeMethod
    public void tryParse() {
        try {
            javaJUnitTestNGHandler = new JavaJUnitTestNG(FileUtils.getResourceFile(getFilePath("TEST-JustTest.xml")), TestNameNodeType.className);
        } catch (Exception e) {
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }

    @Test
    public void validateTests() throws JsonProcessingException {
        List<TestDto> actualTests = javaJUnitTestNGHandler.getTests();
        assertEquals(mapper.serialize(actualTests), FileUtils.getResourceFileAsString(getFilePath("testWithClassName.json")));
    }

    @Test
    public void validateTestSuite() throws JsonProcessingException {
        TestSuiteDto actualTestSuite = javaJUnitTestNGHandler.getTestSuite();
        assertEquals(mapper.serialize(actualTestSuite), FileUtils.getResourceFileAsString(getFilePath("suite.json")));
    }

    @Test
    public void validateTestRun() throws JsonProcessingException {
        TestRunDto actualTestRun = javaJUnitTestNGHandler.getTestRun();
        assertEquals(mapper.serialize(actualTestRun), FileUtils.getResourceFileAsString(getFilePath("testrun.json")));
    }

    @Test
    public void validateTestResults() throws JsonProcessingException {
        List<TestResultDto> actualTestResults = javaJUnitTestNGHandler.getTestResults();
        assertEquals(mapper.serialize(actualTestResults), FileUtils.getResourceFileAsString(getFilePath("results.json")));
    }

    private String getFilePath(String fileName) {
        return "reports/JUnit/".concat(fileName);
    }
}
