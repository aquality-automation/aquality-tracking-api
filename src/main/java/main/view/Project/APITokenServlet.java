package main.view.Project;

import main.Session;
import main.model.dto.project.APITokenDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project/apiToken")
public class APITokenServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            APITokenDto tokenDto = new APITokenDto();
            tokenDto.getSearchTemplateFromRequestParameters(req);
            tokenDto.setApi_token(session.controllerFactory.getHandler(tokenDto).create(tokenDto).getApi_token());
            resp.getWriter().write(mapper.serialize(tokenDto));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
    }
}
