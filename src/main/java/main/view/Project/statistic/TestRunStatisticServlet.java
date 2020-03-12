package main.view.Project.statistic;

import main.Session;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestRunStatisticDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/stats/testrun")
public class TestRunStatisticServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            TestRunStatisticDto testRunStatistic = new TestRunStatisticDto();
            testRunStatistic.getSearchTemplateFromRequestParameters(req);
            List<TestRunStatisticDto> testRunStatistics = session.controllerFactory.getHandler(new TestRunDto()).get(testRunStatistic);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testRunStatistics));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
    }
}
