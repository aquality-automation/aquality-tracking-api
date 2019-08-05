package main.view.ApplicationSettings;

import main.Session;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/authInfo")
public class MainAuthInfoServlet extends BaseServlet implements IGet {
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
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }

}
