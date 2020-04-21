package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.TestResultDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@WebServlet("/public/test/result/finish")
public class PublicTestResultFinishServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);

            TestResultDto testResult = mapper.mapObject(TestResultDto.class, requestedJson);
            validatePost(testResult);

            TestResultDto testResultSearchTemplate = new TestResultDto();
            testResultSearchTemplate.setProject_id(testResult.getProject_id());
            testResultSearchTemplate.setId(testResult.getId());

            List<TestResultDto> oldResults = session.controllerFactory.getHandler(testResult).get(testResultSearchTemplate);

            if(oldResults.size() > 0) {
                TestResultDto currentResult = oldResults.get(0);
                currentResult.setFinal_result_id(testResult.getFinal_result_id());
                currentResult.setFail_reason(testResult.getFail_reason());
                currentResult.setFinish_date(new Date());

                testResult = session.controllerFactory.getHandler(testResult).create(currentResult);
            } else {
                throw new AqualityParametersException("Test Result with %s id was not found!", testResult.getId());
            }

            setResponseBody(resp, testResult);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validatePost(TestResultDto testResult) throws AqualityParametersException {
        if(testResult.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }

        if(testResult.getId() == null) {
            throw new AqualityParametersException("You should specify 'id'!");
        }

        if(testResult.getFinal_result_id() == null) {
            throw new AqualityParametersException("You should specify 'final_result_id' - Failed: 1, Passed: 2, Not Executed: 3, Pending: 5.");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
