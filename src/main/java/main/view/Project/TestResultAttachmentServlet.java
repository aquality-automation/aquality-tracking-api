package main.view.Project;

import main.Session;
import main.model.dto.project.TestResultAttachmentDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@WebServlet("/testresult/attachment")
@MultipartConfig
public class TestResultAttachmentServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestResultAttachmentDto testResultAttachmentDto = new TestResultAttachmentDto();
            testResultAttachmentDto.getSearchTemplateFromRequestParameters(req);
            List<TestResultAttachmentDto> attachments = session.controllerFactory.getHandler(testResultAttachmentDto).get(testResultAttachmentDto);
            resp.getWriter().write(mapper.serialize(attachments));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}


