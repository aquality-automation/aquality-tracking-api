package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.AppSettingsDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/settings")
public class GeneralSettingsServlet extends BaseServlet implements IGet, IPost {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(session.getSettingsController().getApp()));
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
            AppSettingsDto appSettingsDto = mapper.mapObject(AppSettingsDto.class, requestedJson);
            session.getSettingsController().create(appSettingsDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
