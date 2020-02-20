package main.view.Project;


import main.Session;
import main.exceptions.AqualityException;
import main.model.dto.TestDto;
import main.model.dto.TestRunDto;
import main.model.dto.TestSuiteDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/suite/sync")
public class SyncSuiteServlet extends BaseServlet implements IGet, IPost {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Integer notExecutedFor;
            Session session = createSession(req);
            if(!req.getParameterMap().containsKey("notExecutedFor")){
                throw new AqualityException("notExecutedFor is required parameter for Sync Suite");
            }
            notExecutedFor = getIntegerQueryParameter(req,"notExecutedFor");
            Integer suiteId = getIntegerQueryParameter(req, "suiteId");
            TestSuiteDto testSuite = new TestSuiteDto();
            testSuite.getSearchTemplateFromRequestParameters(req);
            List<TestDto> legacyTests = session.controllerFactory.getHandler(testSuite).findLegacyTests(suiteId, notExecutedFor);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(legacyTests));
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
            boolean removeNotExecutedResults = getBooleanQueryParameter(req, "removeNotExecutedResults");
            Integer suiteId = getIntegerQueryParameter(req, "suiteId");
            List<TestDto> legacyTests = mapper.mapObjects(TestDto.class, requestedJson);
            session.controllerFactory.getHandler(new TestSuiteDto()).syncLegacyTests(legacyTests, suiteId, removeNotExecutedResults);
            setJSONContentType(resp);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
