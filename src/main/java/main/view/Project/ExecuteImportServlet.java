package main.view.Project;


import main.Session;
import main.controllers.Project.SuiteController;
import main.controllers.Project.TestRunController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityQueryParameterException;
import main.model.db.imports.Importer;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.project.ImportDto;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestSuiteDto;
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
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {

    private Integer projectId;
    private String buildName;
    private String author;
    private String suiteName;
    private Integer testRunId;
    private Boolean addToLastTestRun;
    private Boolean debug;
    private String environment;
    private String cilink;
    private Boolean singleTestRun;
    private String format;
    private String importToken;
    private SuiteController suiteController;
    private TestRunController testRunController;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            readParameters(req);
            Session session = createSession(req);

            suiteController = session.controllerFactory.getHandler(new TestSuiteDto());
            testRunController = session.controllerFactory.getHandler(new TestRunDto());

            List<String> filePaths = doUpload(req, resp, projectId);

            Importer importer = session.getImporter(
                    filePaths,
                    prepareTestRun(),
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

    private void readParameters(HttpServletRequest req) throws AqualityQueryParameterException {
        singleTestRun = getBooleanQueryParameter(req, ImportParams.singleTestRun.name());
        format = getStringQueryParameter(req, ImportParams.format.name());
        importToken = getStringQueryParameter(req, ImportParams.importToken.name());
        projectId = getIntegerQueryParameter(req, ImportParams.projectId.name());
        buildName = getStringQueryParameter(req, ImportParams.buildName.name());
        author = getStringQueryParameter(req, ImportParams.author.name());
        suiteName = getStringQueryParameter(req, ImportParams.suite.name());
        testRunId = getIntegerQueryParameter(req, ImportParams.testRunId.name());
        addToLastTestRun = getBooleanQueryParameter(req, ImportParams.addToLastTestRun.name());
        environment = getStringQueryParameter(req, ImportParams.environment.name());
        cilink = getStringQueryParameter(req, ImportParams.cilink.name());
        debug = getBooleanQueryParameter(req, ImportParams.debug.name());
        validateRequest();
    }

    private TestRunDto prepareTestRun() throws AqualityException {
        TestSuiteDto testSuiteTemplate = new TestSuiteDto();
        testSuiteTemplate.setName(suiteName);
        testSuiteTemplate.setProject_id(projectId);

        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setProject_id(projectId);
        testRunTemplate.setBuild_name(buildName);
        testRunTemplate.setCi_build(cilink);
        testRunTemplate.setAuthor(author);
        testRunTemplate.setExecution_environment(environment);
        testRunTemplate.setTest_suite(testSuiteTemplate);
        testRunTemplate.setId(getTestRunId());
        testRunTemplate.setDebug(debug ? 1 : 0);

        return testRunTemplate;
    }

    private Integer getTestRunId() throws AqualityException {
        if(testRunId != null) {
            return testRunId;
        }

        if(addToLastTestRun) {
            return testRunController.getLastSuiteTestRun(suiteController.get(suiteName, projectId).getId(), projectId).getId();
        }

        return null;
    }

    private void validateRequest() throws AqualityQueryParameterException {
        if(importToken != null)
        {
            throw new AqualityQueryParameterException("Import Token is deprecated. Follow instructions on the API Token page.");
        }

        if(singleTestRun)
        {
            if (projectId == null || format == null || buildName == null || author == null){
                throw new AqualityQueryParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        }else{
            if (projectId == null || format == null){
                throw new AqualityQueryParameterException("ProjectId or/and Format parameters are missed.");
            }
        }
        if(format.equals(ImportTypes.MSTest.name()) || format.equals(ImportTypes.Cucumber.name())){
            if (suiteName == null){
                throw new AqualityQueryParameterException("Suite parameter is missed.");
            }
        }
    }

    private List<String> doUpload(HttpServletRequest req, HttpServletResponse resp, Integer projectId) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();
        return fileUtils.doUpload(req, resp, PathUtils.createPathToBin("temp", projectId.toString(), String.valueOf(new Date().getTime())));
    }

    private void cleanup(List<String> filePaths){
        if(filePaths.size() > 0) {
            FileUtils fileUtils = new FileUtils();
            String fileFolderPath = fileUtils.getFileFolderPath(filePaths.get(0));
            fileUtils.removeFiles(filePaths);
            fileUtils.removeFile(fileFolderPath);
        }
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req) {
        String testNameKey = getStringQueryParameter(req, ImportParams.testNameKey.name());

        if(testNameKey == null){
            if(format.equals(ImportTypes.NUnit_v3.name())) {
                return TestNameNodeType.featureNameTestName;
            }
            return null;
        }

        try {
            return TestNameNodeType.valueOf(testNameKey);
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("TestNameKey parameter you provide is not correct. The correct values are:'testName', 'className', 'descriptionNode', 'featureNameTestName'.");
        }
    }
}