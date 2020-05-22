package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.audit.AuditAttachmentDto;
import main.model.dto.project.TestResultAttachmentDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestSuiteDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;
import main.view.Project.TestResult;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/public/test/result/attachment")
@MultipartConfig
public class PublicTestResultAttachment extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            setEncoding(resp);
            setPostResponseHeaders(resp);
            Session session = createSession(req);
            TestResultAttachmentDto attachment = new TestResultAttachmentDto();
            attachment.getSearchTemplateFromRequestParameters(req);
            validatePost(attachment);

            FileUtils fileUtils = new FileUtils();
            List<String> filePaths = fileUtils.doUpload(req, resp, PathUtils.createPathToBin(
                    "project",
                    getStringQueryParameter(req, "project_id"),
                    "result_attachments",
                    getStringQueryParameter(req, "test_result_id")));
            List<TestResultAttachmentDto> listOfAttachments = new ArrayList<>();
            for (String path : filePaths) {
                TestResultAttachmentDto newAttachment = new TestResultAttachmentDto();
                newAttachment.getSearchTemplateFromRequestParameters(req);
                newAttachment.setPath(path);
                listOfAttachments.add(newAttachment);
            }

            session.controllerFactory.getHandler(new TestResultDto()).createMultiple(listOfAttachments);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }



    private void validatePost(TestResultAttachmentDto attachment) throws AqualityParametersException {
        if(attachment.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }
        if(attachment.getTest_result_id() == null ) {
            throw new AqualityParametersException("You should specify 'test_result_id!");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}


