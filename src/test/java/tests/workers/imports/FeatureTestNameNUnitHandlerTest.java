package tests.workers.imports;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.model.db.imports.ImportHandlers.NUnitV3;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.*;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import utils.FileUtils;

import java.util.List;

public class FeatureTestNameNUnitHandlerTest {
    private NUnitV3 nUnitV3;
    private  DtoMapperGeneral mapper = new DtoMapperGeneral();

    @BeforeMethod
    public void tryParse(){
        try {
            nUnitV3 = new NUnitV3(FileUtils.getResourceFile("reports/Nunit3/Nunit3.xml"), TestNameNodeType.featureNameTestName);
        } catch (Exception e){
            assertNull(e, String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }

    @Test
    public void validateTests() throws JsonProcessingException {
        List<TestDto> actualTests = nUnitV3.getTests();
        assertEquals(mapper.serialize(actualTests), FileUtils.getResourceFileAsString("reports/Nunit3/tests.json"));
    }

    @Test
    public void validateTestSuite() throws JsonProcessingException {
        TestSuiteDto actualTestSuite = nUnitV3.getTestSuite();
        assertEquals(mapper.serialize(actualTestSuite), FileUtils.getResourceFileAsString("reports/Nunit3/suite.json"));
    }

    @Test
    public void validateTestRun() throws JsonProcessingException {
        TestRunDto actualTestRun = nUnitV3.getTestRun();
        assertEquals(mapper.serialize(actualTestRun), FileUtils.getResourceFileAsString("reports/Nunit3/testRun.json"));
    }

    @Test
    public void validateTestResults() throws JsonProcessingException {
        List<TestResultDto> actualTestResults = nUnitV3.getTestResults();
        assertEquals(mapper.serialize(actualTestResults), FileUtils.getResourceFileAsString("reports/Nunit3/testResults.json"));
    }
}
