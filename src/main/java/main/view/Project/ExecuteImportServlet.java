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
import java.util.UUID;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            ImportRequestParams params = readParameters(req);
            Session session = createSession(req);

            SuiteController suiteController = session.controllerFactory.getHandler(new TestSuiteDto());
            TestRunController testRunController = session.controllerFactory.getHandler(new TestRunDto());

            List<String> filePaths = doUpload(req, resp, params.projectId);

            Importer importer = session.getImporter(
                    filePaths,
                    prepareTestRun(params, suiteController, testRunController),
                    getStringQueryParameter(req, ImportParams.pattern.name()),
                    params.format,
                    getTestNameNodeType(req, params.format),
                    params.singleTestRun
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

    private ImportRequestParams readParameters(HttpServletRequest req) throws AqualityQueryParameterException {
        ImportRequestParams params = new ImportRequestParams();
        params.singleTestRun = getBooleanQueryParameter(req, ImportParams.singleTestRun.name());
        params.format = getStringQueryParameter(req, ImportParams.format.name());
        params.importToken = getStringQueryParameter(req, ImportParams.importToken.name());
        params.projectId = getIntegerQueryParameter(req, ImportParams.projectId.name());
        params.buildName = getStringQueryParameter(req, ImportParams.buildName.name());
        params.author = getStringQueryParameter(req, ImportParams.author.name());
        params.suiteName = getStringQueryParameter(req, ImportParams.suite.name());
        params.testRunId = getIntegerQueryParameter(req, ImportParams.testRunId.name());
        params.addToLastTestRun = getBooleanQueryParameter(req, ImportParams.addToLastTestRun.name());
        params.environment = getStringQueryParameter(req, ImportParams.environment.name());
        params.cilink = getStringQueryParameter(req, ImportParams.cilink.name());
        params.debug = getBooleanQueryParameter(req, ImportParams.debug.name());
        validateRequest(params);
        return params;
    }

    private TestRunDto prepareTestRun(ImportRequestParams params, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        TestSuiteDto testSuiteTemplate = new TestSuiteDto();
        testSuiteTemplate.setName(params.suiteName);
        testSuiteTemplate.setProject_id(params.projectId);

        TestRunDto testRunTemplate = new TestRunDto();
        testRunTemplate.setProject_id(params.projectId);
        testRunTemplate.setBuild_name(params.buildName);
        testRunTemplate.setCi_build(params.cilink);
        testRunTemplate.setAuthor(params.author);
        testRunTemplate.setExecution_environment(params.environment);
        testRunTemplate.setTest_suite(testSuiteTemplate);
        testRunTemplate.setId(getTestRunId(params, suiteController, testRunController));
        testRunTemplate.setDebug(params.debug ? 1 : 0);

        return testRunTemplate;
    }

    private Integer getTestRunId(ImportRequestParams params, SuiteController suiteController, TestRunController testRunController) throws AqualityException {
        if (params.testRunId != null) {
            return params.testRunId;
        }

        if (params.addToLastTestRun) {
            return testRunController.getLastSuiteTestRun(suiteController.get(params.suiteName, params.projectId).getId(), params.projectId).getId();
        }

        return null;
    }

    private void validateRequest(ImportRequestParams params) throws AqualityQueryParameterException {
        if (params.importToken != null) {
            throw new AqualityQueryParameterException("Import Token is deprecated. Follow instructions on the API Token page.");
        }

        if (params.singleTestRun) {
            if (params.projectId == null || params.format == null || params.buildName == null || params.author == null) {
                throw new AqualityQueryParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        } else {
            if (params.projectId == null || params.format == null) {
                throw new AqualityQueryParameterException("ProjectId or/and Format parameters are missed.");
            }
        }
        if (params.format.equals(ImportTypes.MSTest.name()) || params.format.equals(ImportTypes.Cucumber.name())) {
            if (params.suiteName == null) {
                throw new AqualityQueryParameterException("Suite parameter is missed.");
            }
        }
    }

    private List<String> doUpload(HttpServletRequest req, HttpServletResponse resp, Integer projectId) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();
        // UUID keeps parallel imports from sharing/cleaning the same temp folder
        return fileUtils.doUpload(req, resp, PathUtils.createPathToBin("temp", projectId.toString(), UUID.randomUUID().toString()));
    }

    private void cleanup(List<String> filePaths) {
        if (filePaths.size() > 0) {
            FileUtils fileUtils = new FileUtils();
            String fileFolderPath = fileUtils.getFileFolderPath(filePaths.get(0));
            fileUtils.removeFiles(filePaths);
            fileUtils.removeFile(fileFolderPath);
        }
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req, String format) {
        String testNameKey = getStringQueryParameter(req, ImportParams.testNameKey.name());

        if (testNameKey == null) {
            if (format.equals(ImportTypes.NUnit_v3.name())) {
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

    private static class ImportRequestParams {
        Integer projectId;
        String buildName;
        String author;
        String suiteName;
        Integer testRunId;
        Boolean addToLastTestRun;
        Boolean debug;
        String environment;
        String cilink;
        Boolean singleTestRun;
        String format;
        String importToken;
    }
}
