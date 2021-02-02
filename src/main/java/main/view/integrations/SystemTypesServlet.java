package main.view.integrations;

import main.Session;
import main.model.dto.integrations.types.SystemTypeDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/integration/system/types")
public class SystemTypesServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            List<SystemTypeDto> stepTypes = session.controllerFactory.getHandler(new SystemTypeDto()).get(new SystemTypeDto());
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
