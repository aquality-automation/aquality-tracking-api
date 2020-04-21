package main.view.Project;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.TestDto;
import main.model.dto.project.TestRunDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/test/issue")
public class TestIssueServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {

            Session session = createSession(req);
            Integer issueId = getIntegerQueryParameter(req, "issueId");
            Integer projectId = getIntegerQueryParameter(req, "projectId");
            validateGet(issueId, projectId);
            List<TestDto> tests = session.controllerFactory.getHandler(new TestDto()).get(issueId, projectId);
            setResponseBody(resp, tests);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }

    private void validateGet(Integer issueId, Integer projectId) throws AqualityParametersException {
        if(projectId == null) {
            throw new AqualityParametersException("You should specify 'projectId'!");
        }

        if(issueId == null) {
            throw new AqualityParametersException("You should specify 'issueId'!");
        }
    }
}
