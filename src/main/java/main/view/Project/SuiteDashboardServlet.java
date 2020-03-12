package main.view.Project;

import main.Session;
import main.model.dto.project.SuiteDashboardDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@WebServlet("/suite/dashboard")
public class SuiteDashboardServlet extends BaseServlet implements IDelete, IGet, IPost {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            SuiteDashboardDto template = new SuiteDashboardDto();
            template.getSearchTemplateFromRequestParameters(req);
            List<SuiteDashboardDto> dashboards = session.controllerFactory.getHandler(template).get(template);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(dashboards));
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
            SuiteDashboardDto template = mapper.mapObject(SuiteDashboardDto.class, requestedJson);
            template = session.controllerFactory.getHandler(template).create(template);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(template));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            SuiteDashboardDto template = new SuiteDashboardDto();
            template.getSearchTemplateFromRequestParameters(req);
            session.controllerFactory.getHandler(template).delete(template);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
