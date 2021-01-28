package main.view.Integrations;

import main.Session;
import main.model.dto.integrations.IntegrationSystemDto;
import main.model.dto.project.StepTypeDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/integration/systems")
public class IntegrationSystemServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            List<IntegrationSystemDto> stepTypes = session.controllerFactory.getHandler(new IntegrationSystemDto()).get(new IntegrationSystemDto());
            resp.getWriter().write(mapper.serialize(stepTypes));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
