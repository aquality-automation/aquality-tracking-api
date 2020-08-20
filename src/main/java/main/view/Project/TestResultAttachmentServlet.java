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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testresult/attachment")
@MultipartConfig
public class TestResultAttachmentServlet extends BaseServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestResultAttachmentDto attachTemplate = new TestResultAttachmentDto();
            attachTemplate.setId(getIntegerQueryParameter(req, "id"));
            attachTemplate.setProject_id(getIntegerQueryParameter(req, "project_id"));
            validateGet(attachTemplate);
            TestResultAttachmentDto attachmentDto = session.controllerFactory.getHandler(attachTemplate).get(attachTemplate).get(0);
            File file = new File(attachmentDto.getPath());
            ServletContext context = getServletContext();
            attachmentDto.setAttachment(Files.readAllBytes(file.toPath()));
            attachmentDto.setMimeType(context.getMimeType(attachmentDto.getPath()));
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(attachmentDto));
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
