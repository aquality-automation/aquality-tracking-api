package main.view.Project;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.TestResultAttachmentDto;
import main.model.dto.project.TestResultDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;

import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testresult/attachment")
@MultipartConfig
public class TestResultAttachmentServlet extends BaseServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        ServletContext context= req.getServletContext();
        try {
            Session session = createSession(req);
            TestResultAttachmentDto testResultAttachmentDto = new TestResultAttachmentDto();
            testResultAttachmentDto.getSearchTemplateFromRequestParameters(req);
            List<TestResultAttachmentDto> attachments = session.controllerFactory.getHandler(new TestResultDto()).get(testResultAttachmentDto);
            if (attachments.size() > 0) {
                TestResultAttachmentDto attachment = attachments.get(0);
                String mime = context.getMimeType(attachment.getPath());
                File file = new File(attachment.getPath());
                resp.setContentLength((int)file.length());
                resp.setContentType(mime);

                FileInputStream in = new FileInputStream(file);
                OutputStream out = resp.getOutputStream();
                byte[] buf = new byte[1024];
                int count;
                while ((count = in.read(buf)) >= 0) {
                    out.write(buf, 0, count);
                }
                out.close();
                in.close();
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            TestResultAttachmentDto testResultAttachmentDto = new TestResultAttachmentDto();
            testResultAttachmentDto.getIDTemplateFromRequestParameters(req);
            validateDelete(testResultAttachmentDto);
            session.controllerFactory.getHandler(new TestResultDto()).delete(testResultAttachmentDto);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }


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

    private void validateGet(TestResultAttachmentDto attachment) throws AqualityParametersException {
        if(attachment.getId() == null) {
            throw new AqualityParametersException("You should specify 'id'!");
        }
        if(attachment.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }
    }

    private void validateDelete(TestResultAttachmentDto attachment) throws AqualityParametersException {
        if(attachment.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }
        if(attachment.getId() == null ) {
            throw new AqualityParametersException("You should specify 'id!");
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
