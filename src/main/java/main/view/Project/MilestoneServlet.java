package main.view.Project;

import main.Session;
import main.model.dto.MilestoneDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/milestone")
public class MilestoneServlet extends BaseServlet implements IPost, IGet, IDelete {
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);

        try {
            Session session = createSession(req);
            MilestoneDto milestoneDto = new MilestoneDto();
            milestoneDto.setId(Integer.parseInt(req.getParameter("id")));
            session.controllerFactory.getHandler(milestoneDto).delete(milestoneDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            MilestoneDto milestoneTemplate = new MilestoneDto();
            milestoneTemplate.getSearchTemplateFromRequestParameters(req);
            List<MilestoneDto> milestones = session.controllerFactory.getHandler(milestoneTemplate).get(milestoneTemplate);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(milestones));
        } catch (Exception e) {
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
            MilestoneDto milestone = mapper.mapObject(MilestoneDto.class, requestedJson);
            milestone = session.controllerFactory.getHandler(milestone).create(milestone);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(milestone));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
