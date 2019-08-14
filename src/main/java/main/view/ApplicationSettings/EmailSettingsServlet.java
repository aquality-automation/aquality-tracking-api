package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.EmailSettingsDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/settings/email")
public class EmailSettingsServlet extends BaseServlet implements IGet, IPost {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            setJSONContentType(resp);
            EmailSettingsDto emailSettings = new EmailSettingsDto();
            emailSettings.setId(1);
            emailSettings = session.controllerFactory.getHandler(emailSettings).get(emailSettings).get(0);
            emailSettings.setPassword("");
            resp.getWriter().write(mapper.serialize(emailSettings));
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
            EmailSettingsDto emailSettingsDto = mapper.mapObject(EmailSettingsDto.class, requestedJson);
            session.controllerFactory.getHandler(new EmailSettingsDto()).create(emailSettingsDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
