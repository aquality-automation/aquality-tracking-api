package controllers;

import main.controllers.Project.TestController;
import main.exceptions.AqualityException;
import main.model.dto.DtoMapper;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.settings.UserDto;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.FileUtils.getResourceFileAsString;

public class TestControllerTest extends TestController {
    public TestControllerTest() {
        super(new UserDto());
    }

    private DtoMapper<TestDto> testMapper = new DtoMapper<>(TestDto.class);

    @Test
    public void moveTestTest() throws AqualityException {
        List<TestDto> tests = testMapper.mapObjects(getResourceFileAsString("entities/moveTest.json"));
        List<TestResultDto> resultsToMove = getResultsToMove(tests.get(0), tests.get(1));
        assertEquals(resultsToMove.size(), 4);
        for (TestResultDto resultToMove : resultsToMove) {
            assertEquals(resultToMove.getTest_id(), tests.get(1).getId());
        }
    }

}
