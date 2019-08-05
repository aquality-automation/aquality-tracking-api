package main.view.Project;


import main.Session;
import main.model.db.imports.Importer;
import main.model.db.imports.enums.TestNameNodeType;
import main.model.dto.ImportTokenDto;
import main.model.dto.TestRunDto;
import main.model.dto.TestSuiteDto;
import main.utils.FileUtils;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

@WebServlet("/import")
@MultipartConfig
public class ExecuteImportServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            Session session;
            TestRunDto testRunTemplate = new TestRunDto();
            TestSuiteDto testSuiteTemplate = new TestSuiteDto();
            testRunTemplate.setProject_id(Integer.parseInt(req.getParameter("projectId")));
            ValidateRequest(req);
            if(req.getParameterMap().containsKey("importToken")){
                session = new Session();
                session.controllerFactory.getHandler(new ImportTokenDto()).isTokenValid(req.getParameter("importToken"),testRunTemplate.getProject_id());
            }else{
                session = createSession(req);
            }

            List<String> filePaths = doUpload(req, resp);
            testRunTemplate.setBuild_name(req.getParameter("buildName"));
            testRunTemplate.setCi_build(req.getParameter("cilink"));
            testRunTemplate.setAuthor(req.getParameter("author"));
            testRunTemplate.setExecution_environment(req.getParameter("environment"));
            testSuiteTemplate.setName(req.getParameter("suite"));
            testRunTemplate.setTest_suite(testSuiteTemplate);
            if(req.getParameterMap().containsKey("testRunId")) testRunTemplate.setId(Integer.parseInt(req.getParameter("testRunId")));
            Importer importer = session.getImporter(filePaths,
                    testRunTemplate,
                    req.getParameter("pattern"),
                    req.getParameter("format"),
                    getTestNameNodeType(req),
                    Boolean.parseBoolean(req.getParameter("singleTestRun")));
            importer.executeImport();
            FileUtils fileUtils = new FileUtils();
            fileUtils.removeFiles(filePaths);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
    }

    private void ValidateRequest(HttpServletRequest req) throws InvalidParameterException{
        if(req.getParameterMap().containsKey("singleTestRun") && req.getParameter("singleTestRun").equals("true"))
        {
            if (req.getParameter("projectId")== null || req.getParameter("format") == null || req.getParameter("buildName") == null || req.getParameter("author") == null){
                throw new InvalidParameterException("ProjectId or/and Format or/and Author or/and BuildName parameters are missed.");
            }
        }else{
            if (req.getParameter("projectId")== null || req.getParameter("format") == null){
                throw new InvalidParameterException("ProjectId or/and Format parameters are missed.");
            }
        }
        if(Objects.equals(req.getParameter("format"), "MSTest") || Objects.equals(req.getParameter("format"), "Cucumber")){
            if (req.getParameter("suite")== null){
                throw new InvalidParameterException("Suite parameter is missed.");
            }
        }
    }

    private List<String> doUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileUtils fileUtils = new FileUtils();

        String projectId = request.getParameter("projectId");
        final String path = System.getProperty("user.dir") + File.separator + "temp" + File.separator + projectId;

        return fileUtils.doUpload(request, response, path);
    }

    private TestNameNodeType getTestNameNodeType(HttpServletRequest req){
        if(req.getParameterMap().containsKey("testNameKey")){
            switch (req.getParameter("testNameKey")){
                case "testName":
                    return TestNameNodeType.testName;
                case "className":
                    return TestNameNodeType.className;
                case "descriptionNode":
                    return TestNameNodeType.descriptionNode;
                default:
                    throw new InvalidParameterException("TestNameKey parameter you provide is not correct. The correct values are:'testName', 'className', 'descriptionNode'.");
            }
        }
        return null;
    }
}