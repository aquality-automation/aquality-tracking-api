package main.view.Project;

import main.Session;
import main.exceptions.RPException;
import main.model.dto.TestDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test/move")
public class MoveTestServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        try {
            validateGet(req);
            Session session = createSession(req);
            session.controllerFactory.getHandler(new TestDto()).moveTest(
                    Integer.parseInt(req.getParameter("from")),
                    Integer.parseInt(req.getParameter("to")),
                    Boolean.parseBoolean(req.getParameter("remove")),
                    Integer.parseInt(req.getParameter("projectId")));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }

    private void validateGet(HttpServletRequest req) throws RPException {
        assertRequiredField(req, "from");
        assertRequiredField(req, "to");
        assertRequiredField(req, "remove");
        assertRequiredField(req, "projectId");
    }
}
