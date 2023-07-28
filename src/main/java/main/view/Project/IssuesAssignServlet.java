package main.view.Project;

import main.Session;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestRunDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/issues/assign")
public class IssuesAssignServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            Map<String, Integer> results = new HashMap<>();
            Integer testRunId = getIntegerQueryParameter(req, "testRunId");
            Integer testResultId = getIntegerQueryParameter(req, "testResultId");
            if (testRunId != null) {
                TestRunDto testRun = new TestRunDto();
                results = session.controllerFactory.getHandler(testRun).matchIssues(testRunId);
            } else if (testResultId != null) {
                TestResultDto testResult = new TestResultDto();
                results = session.controllerFactory.getHandler(testResult).matchIssues(testResultId);
            }
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(results));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
