package main.view.ApplicationSettings;

import main.Session;
import main.model.dto.LdapDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/settings/ldap")
public class LDAPSettingsServlet extends BaseServlet implements IGet, IPost {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            setJSONContentType(resp);
            LdapDto ldap = session.getSettingsController().getLdap();
            ldap.setAdminSecret("");
            resp.getWriter().write(mapper.serialize(ldap));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            LdapDto ldap = mapper.mapObject(LdapDto.class, requestedJson);
            session.getSettingsController().create(ldap);
            setJSONContentType(resp);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
