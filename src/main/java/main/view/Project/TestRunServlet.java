package main.view.Project;

import main.Session;
import main.model.dto.project.TestRunDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/testrun")
public class TestRunServlet  extends BaseServlet implements IDelete, IPost, IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestRunDto testRunTemplate = new TestRunDto();
            testRunTemplate.getSearchTemplateFromRequestParameters(req);
            boolean withChildren = false;
            Integer limit = 10000;
            if(req.getParameterMap().containsKey("withChildren")){
                withChildren = getIntegerQueryParameter(req, "withChildren").equals(1);
            }
            if(req.getParameterMap().containsKey("limit")){
                limit = getIntegerQueryParameter(req, "limit");
            }
            List<TestRunDto> testRuns = session.controllerFactory.getHandler(testRunTemplate).get(testRunTemplate, withChildren, limit);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testRuns));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            TestRunDto testRun = new TestRunDto();
            testRun.setId(Integer.parseInt(req.getParameter("id")));
            testRun.setProject_id(getProjectId(req));
            session.controllerFactory.getHandler(testRun).delete(testRun);
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
            TestRunDto testRun = mapper.mapObject(TestRunDto.class, requestedJson);
            testRun = session.controllerFactory.getHandler(testRun).create(testRun);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testRun));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
