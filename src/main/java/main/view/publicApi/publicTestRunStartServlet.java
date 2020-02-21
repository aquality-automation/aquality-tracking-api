package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.db.dao.project.TestRunDao;
import main.model.dto.TestDto;
import main.model.dto.TestRunDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@WebServlet("/public/testrun/start")
public class publicTestRunStartServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);

            TestRunDto testrun = mapper.mapObject(TestRunDto.class, requestedJson);
            validatePost(testrun);

            if(testrun.getStart_time() == null) {
                testrun.setStart_time(new Date());
            }

            testrun.setFinish_time(testrun.getStart_time());

            testrun = session.controllerFactory.getHandler(testrun).create(testrun);

            setResponseBody(resp, testrun);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validatePost(TestRunDto testrun) throws AqualityParametersException {
        if(testrun.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }

        if(testrun.getBuild_name() == null) {
            throw new AqualityParametersException("You should specify 'build_name'!");
        }

        if(testrun.getTest_suite_id() == null) {
            throw new AqualityParametersException("You should specify 'test_suite_id'");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
