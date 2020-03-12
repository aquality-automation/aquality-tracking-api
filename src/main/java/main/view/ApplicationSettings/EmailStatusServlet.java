package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.settings.EmailSettingsDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/settings/email/status")
public class EmailStatusServlet  extends BaseServlet implements IGet{
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            setJSONContentType(resp);
            EmailSettingsDto settings = new EmailSettingsDto();
            settings.setEnabled(session.controllerFactory.getHandler(new EmailSettingsDto()).isEmailEnabled() ? 1 : 0);
            resp.getWriter().write(mapper.serialize(settings));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
