package main.view.Project.statistic;

import main.Session;
import main.model.dto.project.TestResultDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.model.dto.project.TestResultStatDto;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/stats/testresult")
public class TestResultStatisticServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestResultStatDto testResultStatDto = new TestResultStatDto();
            testResultStatDto.setProject_id(getProjectId(req));
            testResultStatDto.setTestrun_started_from_date(req.getParameter("testRunStartedFrom"));
            testResultStatDto.setTestrun_started_to_date(req.getParameter("testRunStartedTo"));
            List<TestResultStatDto> testResultStats = session.controllerFactory.getHandler(new TestResultDto()).get(testResultStatDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testResultStats));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
