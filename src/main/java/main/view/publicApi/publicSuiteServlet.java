package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.TestSuiteDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/public/suite/create-or-update")
public class publicSuiteServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);

            TestSuiteDto testSuite = mapper.mapObject(TestSuiteDto.class, requestedJson);
            validatePost(testSuite);
            testSuite = session.controllerFactory.getHandler(testSuite).createOrUpdate(testSuite);

            setResponseBody(resp, testSuite);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validatePost(TestSuiteDto testSuite) throws AqualityParametersException {
        if(testSuite.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }
        if(testSuite.getId() == null && testSuite.getName() == null) {
            throw new AqualityParametersException("You should specify 'id' or/and 'name' suite parameters!");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
