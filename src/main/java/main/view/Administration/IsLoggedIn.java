package main.view.Administration;

import main.Session;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@WebServlet("/users/isAuthorized")
public class IsLoggedIn extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);
        resp.addHeader("Access-Control-Expose-Headers", "accountMember");
        resp.addHeader("Access-Control-Allow-Headers", "accountMember");
        try {
            Session session = createSession(req);
            if(session.isSessionValid()){
                resp.addHeader("accountMember", "false");
                setJSONContentType(resp);
                String resultArray = mapper.serialize(session.getCurrentUser().toPublic());
                resp.getWriter().write(resultArray);
            }else{
                resp.addHeader("accountMember", "false");
                resp.getWriter().write("{}");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        resp.addHeader("Access-Control-Expose-Headers", "accountMember");
        resp.addHeader("Access-Control-Allow-Headers", "accountMember");
    }
}
