package main.view.Project;

import main.Session;
import main.view.BaseServlet;
import main.view.IPost;
import main.model.dto.TestRunDto;
import main.model.dto.UserDto;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/testrun/report")
public class SendTestRunReportServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);
        try {
            Integer projectId = validateAndGetProjectId(req);
            Session session = createSession(req);
            Integer test_run_id = req.getParameterMap().containsKey("test_run_id") ?  Integer.parseInt(req.getParameter("test_run_id")) : null;
            TestRunDto testRunDto = new TestRunDto();
            testRunDto.setId(test_run_id);
            testRunDto.setProject_id(projectId);
            testRunDto = session.controllerFactory.getHandler(testRunDto).get(testRunDto, true, 1).get(0);
            List<UserDto> users = mapper.mapObjects(UserDto.class, getRequestJson(req));
            session.getTestRunEmails().sendTestRunResultsToTeam(testRunDto, users);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
