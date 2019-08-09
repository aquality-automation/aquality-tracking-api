package main.view.Project;


import main.Session;
import main.exceptions.RPException;
import main.model.db.imports.Importer;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.ImportDto;
import main.model.dto.ImportTokenDto;
import main.model.dto.TestRunDto;
import main.model.dto.TestSuiteDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;
import main.view.IPost;
import main.model.db.imports.ImportTypes;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            Boolean singleTestRun = getBooleanQueryParameter(req, ImportParams.singleTestRun.name());
            Integer projectId = getIntegerQueryParameter(req, ImportParams.projectId.name());
            String format = getStringQueryParameter(req, ImportParams.format.name());
            String buildName = getStringQueryParameter(req, ImportParams.buildName.name());
            String author = getStringQueryParameter(req, ImportParams.author.name());
            String suiteName = getStringQueryParameter(req, ImportParams.suite.name());
            String importToken = getStringQueryParameter(req, ImportParams.importToken.name());
            Integer testRunId = getIntegerQueryParameter(req, ImportParams.testRunId.name());
            Boolean addToLastTestRun = getBooleanQueryParameter(req, ImportParams.addToLastTestRun.name());
            String environment = getStringQueryParameter(req, ImportParams.environment.name());
            String cilink = getStringQueryParameter(req, ImportParams.cilink.name());

            validateRequest(singleTestRun, projectId, format, buildName, author, suiteName);

            Session session;
            if (importToken != null) {
                session = new Session();
                session.controllerFactory.getHandler(new ImportTokenDto()).isTokenValid(importToken, projectId);
            } else {
                session = createSession(req);
            }

            List<String> filePaths = doUpload(req, resp, projectId);

            Importer importer = session.getImporter(
                    filePaths,
                    prepareTestRun(session, testRunId, addToLastTestRun, environment, cilink, projectId, buildName, author, suiteName),
                    getStringQueryParameter(req, ImportParams.pattern.name()),
                    getStringQueryParameter(req, ImportParams.format.name()),
                    getTestNameNodeType(req),
                    singleTestRun
            );

            List<ImportDto> imports = importer.executeImport();
            cleanup(filePaths);

            resp.getWriter().write(mapper.serialize(imports));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
    }

    private TestRunDto prepareTestRun(Session session, Integer testRunId, Boolean addToLastTestRun, String environment, String cilink,
                                      Integer projectId, String buildName, String author, String suiteName) throws RPException {

        TestSuiteDto testSuiteTemplate = new TestSuiteDto();
        testSuiteTemplate.setName(suiteName);

        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setProject_id(projectId);
        testRunTemplate.setBuild_name(buildName);
        testRunTemplate.setCi_build(cilink);
        testRunTemplate.setAuthor(author);
        testRunTemplate.setExecution_environment(environment);
        testRunTemplate.setTest_suite(testSuiteTemplate);
        testRunTemplate.setId(getTestRunId(session, testRunId, addToLastTestRun, testSuiteTemplate));

        return testRunTemplate;
    }

    private Integer getTestRunId(Session session, Integer testRunId, Boolean addToLastTestRun, TestSuiteDto suite) throws RPException {
        if(testRunId != null ){
            return testRunId;
        }

        if(addToLastTestRun) {
            List<TestSuiteDto> testSuites = session.controllerFactory.getHandler(suite).get(suite);
            TestRunDto testRunTemplate = new TestRunDto();
            testRunTemplate.setTest_suite_id(testSuites.get(0).getId());

            List<TestRunDto> testRuns = session.controllerFactory.getHandler(testRunTemplate).get(testRunTemplate);
            if(!testRuns.isEmpty()){
                return testRuns.get(0).getId();
            }
        }

        return null;
    }

    private void validateRequest(Boolean singleTestRun, Integer projectId, String format, String buildName, String author, String suiteName) throws InvalidParameterException{

        if(singleTestRun)
        {
            if (projectId == null || format == null || buildName == null || author == null){
                throw new InvalidParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        }else{
            if (projectId == null || format == null){
                throw new InvalidParameterException("ProjectId or/and Format parameters are missed.");
            }
        }
        if(format.equals(ImportTypes.MSTest.name()) || format.equals(ImportTypes.Cucumber.name())){
            if (suiteName == null){
                throw new InvalidParameterException("Suite parameter is missed.");
            }
        }
    }

    private List<String> doUpload(HttpServletRequest req, HttpServletResponse resp, Integer projectId) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();
        return fileUtils.doUpload(req, resp, PathUtils.createPathToBin("temp", projectId.toString()));
    }

    private void cleanup(List<String> filePaths){
        FileUtils fileUtils = new FileUtils();
        fileUtils.removeFiles(filePaths);
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req) {
        String testNameKey = getStringQueryParameter(req, ImportParams.testNameKey.name());

        if(testNameKey == null){
            return null;
        }

        try {
            return TestNameNodeType.valueOf(testNameKey);
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("TestNameKey parameter you provide is not correct. The correct values are:'testName', 'className', 'descriptionNode'.");
        }
    }
}