package main.view.Project;

import main.Session;
import main.exceptions.AqualityException;
import main.model.dto.audit.AuditAttachmentDto;
import main.model.dto.project.TestResultAttachmentDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@WebServlet("/testresult/attachment/download")
@MultipartConfig
public class TestResultAttachmentDownloadServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
                TestResultAttachmentDto testResultAttachmentDto = new TestResultAttachmentDto();
                testResultAttachmentDto.getSearchTemplateFromRequestParameters(req);
            if (testResultAttachmentDto.getTest_result_id() != null) {
                List<TestResultAttachmentDto> attachments = session.controllerFactory.getHandler(testResultAttachmentDto).get(testResultAttachmentDto);
                processResponse(resp, attachments.get(0).getPath());
            } else {
                resp.setStatus(400);
                throw new AqualityException("You have no specify Test Result ID");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}


