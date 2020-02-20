package main.view.Project;

import main.Session;
import main.model.dto.PredefinedResolutionDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@WebServlet("/project/predefined-resolution")
public class PredefinedResolutionServlet extends BaseServlet implements IGet, IPost, IDelete {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);

        try {
            Session session = createSession(req);
            PredefinedResolutionDto template = new PredefinedResolutionDto();
            template.getSearchTemplateFromRequestParameters(req);
            List<PredefinedResolutionDto> predefinedResolutions = session.controllerFactory.getHandler(template).get(template);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(predefinedResolutions));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);

        try {
            Session session = createSession(req);
            PredefinedResolutionDto template = mapper.mapObject(PredefinedResolutionDto.class, getRequestJson(req));
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
            PredefinedResolutionDto template = new PredefinedResolutionDto();
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
