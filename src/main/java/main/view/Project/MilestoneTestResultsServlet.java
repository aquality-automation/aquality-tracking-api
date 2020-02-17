package main.view.Project;


import main.Session;
import main.model.dto.TestResultDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/milestone/results")
public class MilestoneTestResultsServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Integer projectId = validateAndGetProjectId(req);
            Session session = createSession(req);
            Integer milestoneId = getIntegerQueryParameter(req, "milestoneId");
            List<TestResultDto> testResults = session.controllerFactory.getHandler(new TestResultDto()).getLatestResultsByMilestone(projectId, milestoneId);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testResults));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
