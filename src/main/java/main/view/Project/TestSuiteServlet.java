package main.view.Project;


import main.Session;
import main.model.dto.TestSuiteDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/suite")
public class TestSuiteServlet extends BaseServlet implements IDelete, IPost, IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            boolean withChildren = false;
            if(req.getParameterMap().containsKey("withChildren")){
                withChildren = req.getParameter("withChildren").equals("1");
            }
            TestSuiteDto testSuite = new TestSuiteDto();
            testSuite.getSearchTemplateFromRequestParameters(req);
            List<TestSuiteDto> testSuites = session.controllerFactory.getHandler(testSuite).get(testSuite, withChildren);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testSuites));
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
            TestSuiteDto testSuite = mapper.mapObject(TestSuiteDto.class, requestedJson);
            testSuite = session.controllerFactory.getHandler(testSuite).create(testSuite);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testSuite));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            TestSuiteDto testSuite = new TestSuiteDto();
            testSuite.setId(Integer.parseInt(req.getParameter("id")));
            testSuite.setProject_id(getProjectId(req));
            session.controllerFactory.getHandler(testSuite).delete(testSuite);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
