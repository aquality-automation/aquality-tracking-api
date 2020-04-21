package main.view.Administration;

import main.Session;
import main.model.dto.settings.PasswordDto;
import main.model.dto.settings.UserDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users/password")
public class PasswordServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        resp.addHeader("Access-Control-Expose-Headers", "iio78");
        resp.addHeader("Access-Control-Allow-Headers", "iio78");

        try {
            Session session = createSession(req);
            PasswordDto password = mapper.mapObject(PasswordDto.class, getRequestJson(req));
            UserDto user = session.controllerFactory.getHandler(new UserDto()).updatePassword(password);
            resp.addHeader("iio78", user.getSession_code());
            String resultArray = mapper.serialize(user.toPublic());
            resp.getWriter().write(resultArray);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
        resp.addHeader("Access-Control-Allow-Headers", "iio78");
    }
}
