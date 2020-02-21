package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.TestDto;
import main.model.dto.TestResultDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@WebServlet("/public/test/result/start")
public class publicTestResultStartServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestResultDto testResult = new TestResultDto();
            testResult.getSearchTemplateFromRequestParameters(req);

            validatePost(testResult);

            List<TestResultDto> oldResults = session.controllerFactory.getHandler(testResult).get(testResult);

            if(oldResults.size() > 0) {
                TestResultDto pending = oldResults.stream().filter(result -> result.getPending().equals(1)).findFirst().orElse(null);
                if(pending != null) {
                    testResult.setId(pending.getId());
                }
            }

            testResult.setStart_date(new Date());
            testResult.setFinal_result_id(4);

            testResult = session.controllerFactory.getHandler(testResult).create(testResult);

            setResponseBody(resp, testResult);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validatePost(TestResultDto testResult) throws AqualityParametersException {
        if(testResult.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }

        if(testResult.getTest_id() == null) {
            throw new AqualityParametersException("You should specify 'test_id'");
        }

        if(testResult.getTest_run_id() == null) {
            throw new AqualityParametersException("You should specify 'test_run_id'");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
