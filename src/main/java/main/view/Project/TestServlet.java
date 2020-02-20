package main.view.Project;

import main.Session;
import main.model.dto.TestDto;
import main.model.dto.TestRunDto;
import main.view.BaseServlet;
import main.view.IDelete;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/test")
public class TestServlet extends BaseServlet implements IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {

            Session session = createSession(req);
            TestDto test = new TestDto();
            test.getSearchTemplateFromRequestParameters(req);
            List<TestDto> tests = session.controllerFactory.getHandler(test).get(test);
            resp.getWriter().write(mapper.serialize(tests));
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
            TestDto test = mapper.mapObject(TestDto.class, requestedJson);
            test = session.controllerFactory.getHandler(test).create(test, true);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(test));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            List<TestDto> tests = mapper.mapObjects(TestDto.class, requestedJson);
            session.controllerFactory.getHandler(new TestDto()).updateMultipleTests(tests);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            TestDto test = new TestDto();
            test.setId(Integer.parseInt(req.getParameter("id")));
            test.setProject_id(getProjectId(req));
            session.controllerFactory.getHandler(test).delete(test);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
