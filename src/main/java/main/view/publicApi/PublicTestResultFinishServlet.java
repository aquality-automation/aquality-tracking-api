package main.view.publicApi;

import main.Session;
import main.controllers.Project.ResultController;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.TestResultDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
            testResult.setFinish_date(new Date());

            ResultController testResultController = session.controllerFactory.getHandler(testResult);
            TestResultDto updatedTestResult = testResultController.updateWithFinalResultIdAndFailReason(testResult);

            setResponseBody(resp, updatedTestResult);
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
