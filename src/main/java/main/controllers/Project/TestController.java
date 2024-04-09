package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.*;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestController extends BaseController<TestDto> {
    private final TestDao testDao;
    private final TestSuiteDao suiteDao;
    private final TestResultDao resultDao;
    private final Test2SuiteController test2SuiteController;
    private final ProjectUserController projectUserController;

    public TestController(UserDto user) {
        super(user);

        testDao = new TestDao();
        suiteDao = new TestSuiteDao();
        resultDao = new TestResultDao();
        test2SuiteController = new Test2SuiteController(user);
        projectUserController = new ProjectUserController(user);
    }

    public TestDto create(TestDto template, boolean updateSuites) throws AqualityException {
        checkCreatePermissions(template.getProject_id());
        TestDto test = testDao.create(template);
        if (updateSuites) {
            test.setSuites(template.getSuites());
            updateSuites(test);
            test = get(test).get(0);
        }
        return test;
    }

    public TestDto createOrUpdate(TestDto test) throws AqualityException {
        checkCreatePermissions(test.getProject_id());
        TestDto rawTest = getOrCreateRawTest(test);
        return updateTestSuites(rawTest, test.getSuites().get(0).getId());
    }

    @Override
    public TestDto create(TestDto template) throws AqualityException {
        return create(template, false);
    }

    public List<TestDto> get(TestDto template) throws AqualityException {
        checkReadPermissions(template.getProject_id());
        return fillTests(testDao.searchAll(template));
    }

    public List<TestDto> get(Integer issueId, Integer projectId) throws AqualityException {
        checkReadPermissions(projectId);
        return fillTests(testDao.getTestsAffectedByIssue(issueId));
    }

    @Override
    public boolean delete(TestDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return testDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Test", baseUser);
        }
    }

    public void updateMultipleTests(List<TestDto> entities) throws AqualityException {
        if (!entities.isEmpty() && (baseUser.isManager() || baseUser.getProjectUser(entities.get(0).getProject_id()).isEditor())) {
            for (TestDto test : entities) {
                updateSuites(test);
            }
            testDao.updateMultiply(entities);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to update Test ", baseUser);
        }
    }

    public void moveTest(int from, int to, boolean remove, int projectId) throws AqualityException {
        TestDto oldTest = getTestForMovement(from, projectId);
        TestDto newTest = getTestForMovement(to, projectId);

        executeResultsMovement(getResultsToMove(oldTest, newTest));

        if (remove) {
            delete(oldTest);
        }
    }

    private TestDto getTestForMovement(int id, int projectId) throws AqualityException {
        TestDto test = new TestDto();
        test.setId(id);
        test.setProject_id(projectId);
        test = get(test).get(0);
        TestResultDto resultSearchTemplate = new TestResultDto();
        resultSearchTemplate.setTest_id(test.getId());
        test.setResults(resultDao.searchAll(resultSearchTemplate));
        return test;
    }

    private void executeResultsMovement(List<TestResultDto> resultsToMove) throws AqualityException {
        for (TestResultDto result : resultsToMove) {
            resultDao.create(result);
        }
    }

    protected List<TestResultDto> getResultsToMove(TestDto from, TestDto to) {
        List<TestResultDto> newResults = to.getResults();
        List<TestResultDto> resultsToMove = new ArrayList<>();
        for (TestResultDto result : from.getResults()) {
            TestResultDto existingResult = newResults.stream().filter(x -> x.getTest_run_id().equals(result.getTest_run_id())).findFirst().orElse(null);
            if (existingResult == null) {
                result.setTest_id(to.getId());
                resultsToMove.add(result);
            }
        }

        return resultsToMove;
    }

    private List<TestDto> fillTests(List<TestDto> tests) throws AqualityException {
        List<TestDto> filledTests = new ArrayList<>();
        if (!tests.isEmpty()) {
            Integer projectId = tests.get(0).getProject_id();
            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(tests.get(0).getProject_id());
            List<ProjectUserDto> projectUsers = projectUserController.get(projectUserDto);
            List<TestSuiteDto> testSuites = getProjectTestSuites(projectId);
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(tests.get(0).getProject_id());

            Test2SuiteDto test2Suite = new Test2SuiteDto();
            test2Suite.setProject_id(projectId);
            List<Test2SuiteDto> test2Suites = test2SuiteController.get(test2Suite);

            for (TestDto test : tests) {
                if (test.getDeveloper_id() != null) {
                    test.setDeveloper(projectUsers.stream().filter(x -> x.getUser().getId().equals(test.getDeveloper_id())).findFirst().orElse(null));
                }

                List<Test2SuiteDto> testSuiteLinks = test2Suites.stream().filter(x -> x.getTest_id().equals(test.getId())).collect(Collectors.toList());
                test.setSuites(test2SuiteController.convertToSuites(testSuiteLinks, testSuites));
                filledTests.add(test);
            }
        }
        return filledTests;
    }

    private void updateSuites(TestDto test) throws AqualityException {
        Test2SuiteDto test2SuiteDto = new Test2SuiteDto();
        test2SuiteDto.setTest_id(test.getId());
        List<Test2SuiteDto> oldSuites = test2SuiteController.get(test2SuiteDto);
        if (test.getSuites() != null && !test.getSuites().isEmpty()) {
            List<TestSuiteDto> suites = test.getSuites();
            for (TestSuiteDto newSuite : suites) {
                Test2SuiteDto alreadyExists = oldSuites.stream().filter(x -> Objects.equals(x.getSuite_id(), newSuite.getId())).findAny().orElse(null);
                if (alreadyExists != null) {
                    oldSuites.removeIf(x -> Objects.equals(x.getSuite_id(), alreadyExists.getSuite_id()));
                } else {
                    Test2SuiteDto newTest2Suite = new Test2SuiteDto();
                    newTest2Suite.setSuite_id(newSuite.getId());
                    newTest2Suite.setTest_id(test.getId());
                    test2SuiteController.create(newTest2Suite, test.getProject_id());
                }
            }
        }

        if (!oldSuites.isEmpty()) {
            for (Test2SuiteDto oldSuite : oldSuites) {
                test2SuiteController.delete(oldSuite, test.getProject_id());
            }
        }
    }

    private void checkReadPermissions(Integer projectId) throws AqualityException {
        if (!(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isViewer())) {
            throw new AqualityPermissionsException("Account is not allowed to view Tests", baseUser);
        }
    }

    private void checkCreatePermissions(Integer projectId) throws AqualityException {
        if (!(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(projectId).isEditor())) {
            throw new AqualityPermissionsException("Account is not allowed to create Test", baseUser);
        }
    }

    private List<TestSuiteDto> getProjectTestSuites(Integer projectId) throws AqualityException {
        TestSuiteDto testSuiteDto = new TestSuiteDto();
        testSuiteDto.setProject_id(projectId);
        return suiteDao.searchAll(testSuiteDto);
    }

    private TestDto getOrCreateRawTest(TestDto test) throws AqualityException {
        TestDto searchTemplate = new TestDto();
        searchTemplate.setId(test.getId());
        searchTemplate.setProject_id(test.getProject_id());
        searchTemplate.setName(test.getName());

        List<TestDto> existingTests = testDao.searchAll(searchTemplate);

        return existingTests.isEmpty() ? testDao.create(test) : existingTests.get(0);
    }

    private TestDto updateTestSuites(TestDto testDto, Integer testSuiteId) throws AqualityException {
        Integer projectId = testDto.getProject_id();
        Test2SuiteDto test2SuiteDto = new Test2SuiteDto();
        test2SuiteDto.setProject_id(projectId);
        test2SuiteDto.setTest_id(testDto.getId());

        List<Test2SuiteDto> existingTest2Suites = test2SuiteController.get(test2SuiteDto);
        if (existingTest2Suites.stream().noneMatch(test2Suite -> test2Suite.getSuite_id().equals(testSuiteId))) {
            test2SuiteDto.setSuite_id(testSuiteId);
            Test2SuiteDto createdTest2Suite = test2SuiteController.create(test2SuiteDto, projectId);
            existingTest2Suites.add(createdTest2Suite);
        }

        List<TestSuiteDto> projectTestSuites = getProjectTestSuites(projectId);
        testDto.setSuites(test2SuiteController.convertToSuites(existingTest2Suites, projectTestSuites));

        return testDto;
    }
}
