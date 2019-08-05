package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.EmailSettingsDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/settings/email")
public class EmailSettingsServlet extends BaseServlet implements IGet, IPost {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            setJSONContentType(resp);
            EmailSettingsDto settings = session.getSettingsController().getEmail();
            settings.setPassword("");
            resp.getWriter().write(mapper.serialize(settings));
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
            session.getSettingsController().create(emailSettingsDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
