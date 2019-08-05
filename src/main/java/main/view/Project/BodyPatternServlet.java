package main.view.Project;

import main.Session;
import main.model.dto.BodyPatternDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/body_pattern")
public class BodyPatternServlet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            Integer project_id = getIntegerQueryParameter(req, "projectId");
            BodyPatternDto bodyPatternDto = new BodyPatternDto();
            bodyPatternDto.setProject_id(project_id);
            List<BodyPatternDto> bodyPatterns = session.controllerFactory.getHandler(bodyPatternDto).get(bodyPatternDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(bodyPatterns));
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
            BodyPatternDto bodyPatternDto = mapper.mapObject(BodyPatternDto.class, requestedJson);
            session.controllerFactory.getHandler(bodyPatternDto).create(bodyPatternDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            BodyPatternDto entity = new BodyPatternDto();
            entity.setId(getIntegerQueryParameter(req, "id"));
            session.controllerFactory.getHandler(entity).delete(entity);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
