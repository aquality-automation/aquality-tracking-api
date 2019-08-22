package main.view.Project;

import main.Session;
import main.model.dto.DtoMapper;
import main.model.dto.ImportTokenDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/project/importToken")
public class ImportTokenServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            ImportTokenDto tokenDto = new ImportTokenDto();
            tokenDto.getSearchTemplateFromRequestParameters(req);
            tokenDto.setImport_token(session.controllerFactory.getHandler(tokenDto).create(tokenDto).getImport_token());
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
