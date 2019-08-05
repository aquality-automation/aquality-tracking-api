package main.view.Administration;

import main.Session;
import main.model.dto.UserDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@WebServlet("/users/auth")
public class AuthorizationServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        resp.addHeader("Access-Control-Expose-Headers", "iio78");
        resp.addHeader("Access-Control-Expose-Headers", "accountMember");
        resp.addHeader("Access-Control-Allow-Headers", "iio78");
        resp.addHeader("Access-Control-Allow-Headers", "accountMember");
        setEncoding(resp);

        String authString = req.getParameter("auth");
        boolean ldap = Boolean.parseBoolean(req.getParameter("ldap"));
        try {
            Session session = createSession(req);
            UserDto systemUser = new UserDto();
            systemUser.setId(1);
            systemUser.setAdmin(1);
            session.setCurrentUser(systemUser);
            UserDto user = session.getAdministrationController().auth(authString, ldap);
            session.setCurrentUser(user);
            boolean isMember = session.getProjectPermissions().size() > 0;
            resp.addHeader("accountMember", String.valueOf(isMember));
            setJSONContentType(resp);
            resp.addHeader("iio78", user.getSession_code());
            resp.getWriter().write(mapper.serialize(user.toPublic()));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        resp.addHeader("Access-Control-Allow-Headers", "iio78");
        resp.addHeader("Access-Control-Allow-Headers", "accountMember");
    }
}
