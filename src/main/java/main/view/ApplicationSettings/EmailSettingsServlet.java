package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.settings.EmailSettingsDto;
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
            EmailSettingsDto emailSetting = new EmailSettingsDto();
            emailSetting.setId(1);
            emailSetting = session.controllerFactory.getHandler(emailSetting).get(emailSetting).get(0);
            emailSetting.setPassword(null);
            resp.getWriter().write(mapper.serialize(emailSetting));
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
            EmailSettingsDto emailSetting = mapper.mapObject(EmailSettingsDto.class, requestedJson);
            emailSetting = session.controllerFactory.getHandler(new EmailSettingsDto()).create(emailSetting);
            emailSetting.setPassword(null);
            resp.getWriter().write(mapper.serialize(emailSetting));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
