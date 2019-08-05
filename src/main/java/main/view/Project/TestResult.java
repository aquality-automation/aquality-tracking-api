package main.view.Project;


import main.Session;
import main.model.dto.TestResultDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/testresult")
public class TestResult extends BaseServlet implements IPost, IGet, IDelete {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            TestResultDto testResult = mapper.mapObject(TestResultDto.class, requestedJson);
            testResult = session.controllerFactory.getHandler(testResult).create(testResult);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testResult));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            TestResultDto testResultTemplate = new TestResultDto();
            testResultTemplate.getSearchTemplateFromRequestParameters(req);
            List<TestResultDto> testResults = session.controllerFactory.getHandler(testResultTemplate).get(testResultTemplate, 100000);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testResults));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            List<TestResultDto> testResults = mapper.mapObjects(TestResultDto.class, requestedJson);
            session.controllerFactory.getHandler(new TestResultDto()).updateMultipleTestResults(testResults);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp){
        setDeleteResponseHeaders(resp);

        try {
            Session session = createSession(req);
            TestResultDto testResult = new TestResultDto();
            testResult.setId(Integer.parseInt(req.getParameter("id")));
            testResult.setProject_id(Integer.parseInt(req.getParameter("projectId")));
            session.controllerFactory.getHandler(testResult).delete(testResult);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
