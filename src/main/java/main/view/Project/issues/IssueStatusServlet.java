package main.view.Project.issues;

import main.Session;
import main.model.dto.project.IssueDto;
import main.model.dto.project.IssueStatusDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/issue/status")
public class IssueStatusServlet extends BaseServlet implements IGet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            List<IssueStatusDto> issueStatuses = session.controllerFactory.getHandler(new IssueDto()).get(new IssueStatusDto());
            resp.getWriter().write(mapper.serialize(issueStatuses));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
