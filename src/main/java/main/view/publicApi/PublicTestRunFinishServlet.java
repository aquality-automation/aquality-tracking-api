package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.project.TestRunDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@WebServlet("/public/testrun/finish")
public class PublicTestRunFinishServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestRunDto testrun = new TestRunDto();
            testrun.getSearchTemplateFromRequestParameters(req);

            validateGet(testrun);

            testrun.setFinish_time(new Date());
            testrun = session.controllerFactory.getHandler(testrun).create(testrun);

            setResponseBody(resp, testrun);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validateGet(TestRunDto testrun) throws AqualityParametersException {
        if(testrun.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }

        if(testrun.getId() == null) {
            throw new AqualityParametersException("You should specify 'id'!");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
