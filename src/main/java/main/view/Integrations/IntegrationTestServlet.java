package main.view.Integrations;

import main.Session;
import main.model.dto.integrations.IntegrationTestDto;
import main.view.BaseServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/integration/test")
public class IntegrationTestServlet extends BaseServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            IntegrationTestDto integrationTest = mapper.mapObject(IntegrationTestDto.class, requestedJson);
            integrationTest = session.controllerFactory.getHandler(integrationTest).create(integrationTest);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(integrationTest));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
