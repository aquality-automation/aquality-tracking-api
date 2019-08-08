package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.*;
import main.model.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestController extends BaseController<TestDto> {
    private TestDao testDao;
    private TestSuiteDao suiteDao;
    private ResultController resultController;
    private Test2SuiteController test2SuiteController;
    private ProjectUserController projectUserController;

    public TestController(UserDto user) {
        super(user);
        testDao = new TestDao();
        suiteDao = new TestSuiteDao();
        resultController = new ResultController(user);
        test2SuiteController = new Test2SuiteController(user);
        projectUserController = new ProjectUserController(user);
    }

    public TestDto create(TestDto template, boolean updateSuites) throws RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            TestDto test = testDao.create(template);
            if(updateSuites){
                test.setSuites(template.getSuites());
                updateSuites(test);
            }
            return test;
        }else{
            throw new RPPermissionsException("Account is not allowed to create Test", baseUser);
        }
    }

    @Override
    public TestDto create(TestDto template) throws RPException {
        return create(template, false);
    }

    public List<TestDto> get(TestDto template, boolean withChildren) throws  RPException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return fillTests(testDao.searchAll(template), withChildren);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Milestones", baseUser);
        }
    }

    @Override
    public List<TestDto> get(TestDto template) throws  RPException {
        return get(template, false);
    }

    @Override
    public boolean delete(TestDto template) throws  RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return testDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Test", baseUser);
        }
    }

    public boolean updateMultipleTests(List<TestDto> entities) throws RPException {
        if(entities.size() > 0 && (baseUser.isManager() || baseUser.getProjectUser(entities.get(0).getProject_id()).isEditor())){
            for (TestDto test : entities) {
                updateSuites(test);
            }
            return testDao.updateMultiply(entities);
        }else{
            throw new RPPermissionsException("Account is not allowed to update Test ", baseUser);
        }
    }

    public void moveTest(int from, int to, boolean remove, int projectId) throws RPException {
        TestDto oldTest = new TestDto();
        oldTest.setId(from);
        oldTest.setProject_id(projectId);
        TestDto newTest = new TestDto();
        newTest.setId(to);
        newTest.setProject_id(projectId);
        oldTest = get(oldTest, true).get(0);
        newTest = get(newTest, true).get(0);

        executeResultsMovement(getResultsToMove(oldTest, newTest));

        if(remove){
            delete(oldTest);
        }
    }

    private void executeResultsMovement(List<TestResultDto> resultsToMove) throws RPException {
        for (TestResultDto result : resultsToMove) {
            resultController.create(result);
        }
    }

    protected List<TestResultDto> getResultsToMove (TestDto from, TestDto to){
        List<TestResultDto> newResults = to.getResults();
        List<TestResultDto> resultsToMove = new ArrayList<>();
        for (TestResultDto result : from.getResults()) {
            TestResultDto existingResult = newResults.stream().filter(x -> x.getTest_run_id().equals(result.getTest_run_id())).findFirst().orElse(null);
            if(existingResult == null){
                result.setTest_id(to.getId());
                resultsToMove.add(result);
            }
        }

        return resultsToMove;
    }

    //TODO Refactoring
    private List<TestDto> fillTests(List<TestDto> tests, boolean withChildren) throws RPException {
        List<TestDto> filledTests = new ArrayList<>();
        if (tests.size() > 0) {
            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(tests.get(0).getProject_id());
            List<ProjectUserDto> projectUsers = projectUserController.get(projectUserDto);
            TestSuiteDto testSuiteDto = new TestSuiteDto();
            testSuiteDto.setProject_id(tests.get(0).getProject_id());
            List<TestSuiteDto> testSuites = suiteDao.searchAll(testSuiteDto);
            List<Test2SuiteDto> test2Suites = new ArrayList<>();
            for(TestSuiteDto testSuite : testSuites){
                Test2SuiteDto test2Suite = new Test2SuiteDto();
                test2Suite.setSuite_id(testSuite.getId());
                test2Suites.addAll(test2SuiteController.get(test2Suite));
            }


            for (TestDto test:tests) {
                if (test.getDeveloper_id() != null) {
                    test.setDeveloper(projectUsers.stream().filter(x -> x.getUser().getId().equals(test.getDeveloper_id())).findFirst().orElse(null));
                }

                List<Test2SuiteDto> testSuiteLinks = test2Suites.stream().filter(x -> x.getTest_id().equals(test.getId())).collect(Collectors.toList());
                test.setSuites(test2SuiteController.convertToSuites(testSuiteLinks, testSuites));

                if (withChildren) {
                    TestResultDto testResultTemplate = new TestResultDto();
                    testResultTemplate.setTest_id(test.getId());
                    testResultTemplate.setProject_id(test.getProject_id());
                    testResultTemplate.setLimit(0);
                    test.setResults(resultController.get(testResultTemplate));
                }
                filledTests.add(test);
            }
        }
        return filledTests;
    }

    //TODO Refactoring
    private void updateSuites(TestDto test) throws RPException {
        Test2SuiteDto test2SuiteDto = new Test2SuiteDto();
        test2SuiteDto.setTest_id(test.getId());
        List<Test2SuiteDto> oldSuites = test2SuiteController.get(test2SuiteDto);
        if(test.getSuites() != null && test.getSuites().size() > 0){
            for (TestSuiteDto newSuite : test.getSuites()) {
                Test2SuiteDto alreadyExists = oldSuites.stream().filter(x -> Objects.equals(x.getSuite_id(), newSuite.getId())).findAny().orElse(null);
                if (alreadyExists != null) {
                    oldSuites.removeIf(x -> Objects.equals(x.getSuite_id(), alreadyExists.getSuite_id()));
                } else {
                    Test2SuiteDto newTest2Suite = new Test2SuiteDto();
                    newTest2Suite.setSuite_id(newSuite.getId());
                    newTest2Suite.setTest_id(test.getId());
                    test2SuiteController.create(newTest2Suite);
                }
            }
        }

        if(oldSuites.size() > 0 ){
            for (Test2SuiteDto oldSuite : oldSuites) {
                test2SuiteController.delete(oldSuite);
            }
        }
    }
}
