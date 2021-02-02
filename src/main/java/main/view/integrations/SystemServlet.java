package main.view.integrations;

import main.Session;
import main.model.dto.integrations.SystemDto;
import main.view.BaseServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/integration/systems")
public class SystemServlet extends BaseServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            SystemDto system = mapper.mapObject(SystemDto.class, requestedJson);
            system = session.controllerFactory.getHandler(system).create(system);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(system));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            SystemDto systemDto = new SystemDto();
            systemDto.getSearchTemplateFromRequestParameters(req);
            List<SystemDto> systems = session.controllerFactory.getHandler(systemDto).get(systemDto);
            resp.getWriter().write(mapper.serialize(systems));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
