package main.view.Project;

import main.Session;
import main.model.dto.Test2SuiteDto;
import main.view.BaseServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/testToSuite")
public class AssignSuiteServlet extends BaseServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            Session session = createSession(req);
            Test2SuiteDto test2Suite = new Test2SuiteDto();
            test2Suite.setSuite_id(Integer.parseInt(req.getParameter("suiteId")));
            test2Suite.setTest_id(Integer.parseInt(req.getParameter("testId")));
            session.controllerFactory.getHandler(test2Suite).create(test2Suite, Integer.parseInt(req.getParameter("project_id")));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            Test2SuiteDto test2Suite = new Test2SuiteDto();
            test2Suite.setSuite_id(Integer.parseInt(req.getParameter("suiteId")));
            test2Suite.setTest_id(Integer.parseInt(req.getParameter("testId")));
            session.controllerFactory.getHandler(test2Suite).delete(test2Suite, Integer.parseInt(req.getParameter("project_id")));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
