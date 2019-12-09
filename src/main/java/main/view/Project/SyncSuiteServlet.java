package main.view.Project;


import main.Session;
import main.exceptions.AqualityException;
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
public class SyncSuiteServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Integer notExecutedFor;
            boolean removeNotExecutedResults;
            Session session = createSession(req);
            if(!req.getParameterMap().containsKey("notExecutedFor")){
                throw new AqualityException("notExecutedFor is required parameter for Sync Suite");
            }
            notExecutedFor = getIntegerQueryParameter(req,"notExecutedFor");
            removeNotExecutedResults = getBooleanQueryParameter(req, "removeNotExecutedResults");
            TestSuiteDto testSuite = new TestSuiteDto();
            testSuite.getSearchTemplateFromRequestParameters(req);
            session.controllerFactory.getHandler(testSuite).syncLegacyTests(testSuite, notExecutedFor, removeNotExecutedResults);
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
