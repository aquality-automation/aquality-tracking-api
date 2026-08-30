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
import java.util.List;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            RequestData data = readParameters(req);
            Session session = createSession(req);

            SuiteController suiteController = session.controllerFactory.getHandler(new TestSuiteDto());
            TestRunController testRunController = session.controllerFactory.getHandler(new TestRunDto());

            List<String> filePaths = doUpload(req, resp, data.projectId);

            // If no files were uploaded, do not create import entries (prevents empty/null imports triggered by malformed multipart)
            if (filePaths == null || filePaths.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(mapper.serialize(new ImportDto[]{}));
                return;
            }

            Importer importer = session.getImporter(
                    filePaths,
                    prepareTestRun(data, suiteController, testRunController),
                    getStringQueryParameter(req, ImportParams.pattern.name()),
                    data.format,
                    getTestNameNodeType(req, data.format),
                    data.singleTestRun
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
        setOptionsResponseHeaders(resp);
    }

    private RequestData readParameters(HttpServletRequest req) throws AqualityQueryParameterException {
        RequestData data = new RequestData();
        data.singleTestRun = getBooleanQueryParameter(req, ImportParams.singleTestRun.name());
        data.format = getStringQueryParameter(req, ImportParams.format.name());
        data.importToken = getStringQueryParameter(req, ImportParams.importToken.name());
        data.projectId = getIntegerQueryParameter(req, ImportParams.projectId.name());
        data.buildName = getStringQueryParameter(req, ImportParams.buildName.name());
        data.author = getStringQueryParameter(req, ImportParams.author.name());
        data.suiteName = getStringQueryParameter(req, ImportParams.suite.name());
        data.testRunId = getIntegerQueryParameter(req, ImportParams.testRunId.name());
        data.addToLastTestRun = getBooleanQueryParameter(req, ImportParams.addToLastTestRun.name());
        data.environment = getStringQueryParameter(req, ImportParams.environment.name());
        data.cilink = getStringQueryParameter(req, ImportParams.cilink.name());
        data.debug = getBooleanQueryParameter(req, ImportParams.debug.name());
        validateRequest(data);
        return data;
    }

    private TestRunDto prepareTestRun(RequestData data, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        TestSuiteDto testSuiteTemplate = new TestSuiteDto();
        testSuiteTemplate.setName(data.suiteName);
        testSuiteTemplate.setProject_id(data.projectId);

        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setProject_id(data.projectId);
        testRunTemplate.setBuild_name(data.buildName);
        testRunTemplate.setCi_build(data.cilink);
        testRunTemplate.setAuthor(data.author);
        testRunTemplate.setExecution_environment(data.environment);
        testRunTemplate.setTest_suite(testSuiteTemplate);
        testRunTemplate.setId(getTestRunId(data, suiteController, testRunController));
        testRunTemplate.setDebug(Boolean.TRUE.equals(data.debug) ? 1 : 0);

        return testRunTemplate;
    }

    private Integer getTestRunId(RequestData data, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        if(data.testRunId != null) {
            return data.testRunId;
        }

        if (Boolean.TRUE.equals(data.addToLastTestRun)) {
            return testRunController.getLastSuiteTestRun(suiteController.get(data.suiteName, data.projectId).getId(), data.projectId).getId();
        }

        return null;
    }

    private void validateRequest(RequestData data) throws AqualityQueryParameterException {
        if(data.importToken != null)
        {
            throw new AqualityQueryParameterException("Import Token is deprecated. Follow instructions on the API Token page.");
        }

        if (Boolean.TRUE.equals(data.singleTestRun)) {
            if (data.projectId == null || data.format == null || data.buildName == null || data.author == null){
                throw new AqualityQueryParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        } else {
            if (data.projectId == null || data.format == null){
                throw new AqualityQueryParameterException("ProjectId or/and Format parameters are missed.");
            }
        }
        if ((ImportTypes.MSTest.name().equals(data.format) || ImportTypes.Cucumber.name().equals(data.format)) && data.suiteName == null) {
            throw new AqualityQueryParameterException("Suite parameter is missed.");
        }
    }

    private static class RequestData {
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
    }

    private List<String> doUpload(HttpServletRequest req, HttpServletResponse resp, Integer projectId) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();
        String dest = PathUtils.createPathToBin("temp", projectId.toString(), java.util.UUID.randomUUID().toString());
        return fileUtils.doUpload(req, resp, dest);
    }

    private void cleanup(List<String> filePaths){
        if(filePaths.size() > 0) {
            FileUtils fileUtils = new FileUtils();
            String fileFolderPath = fileUtils.getFileFolderPath(filePaths.get(0));
            fileUtils.removeFiles(filePaths);
            fileUtils.removeFile(fileFolderPath);
        }
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req, String format) {
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