package main.view.Project;

import main.Session;
import main.model.dto.project.StepDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/steps")
public class StepServlet extends BaseServlet implements IGet, IPost, IDelete {


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            StepDto step = new StepDto();
            step.getSearchTemplateFromRequestParameters(req);
            List<StepDto> steps = session.controllerFactory.getHandler(step).get(step);
            resp.getWriter().write(mapper.serialize(steps));
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
            StepDto step = mapper.mapObject(StepDto.class, requestedJson);
            step = session.controllerFactory.getHandler(step).create(step);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(step));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            StepDto step = new StepDto();
            step.setId(Integer.parseInt(req.getParameter("id")));
            step.setProject_id(getProjectId(req));
            session.controllerFactory.getHandler(step).delete(step);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
