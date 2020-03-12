package main.view.Project;

import main.Session;
import main.model.dto.project.IssueDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/issues")
public class IssueServlet extends BaseServlet implements IGet, IPost, IDelete {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            IssueDto issue = new IssueDto();
            issue.getSearchTemplateFromRequestParameters(req);
            List<IssueDto> issues = session.controllerFactory.getHandler(issue).get(issue);
            resp.getWriter().write(mapper.serialize(issues));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            IssueDto issue = mapper.mapObject(IssueDto.class, requestedJson);
            issue = session.controllerFactory.getHandler(issue).create(issue);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(issue));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {

    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
