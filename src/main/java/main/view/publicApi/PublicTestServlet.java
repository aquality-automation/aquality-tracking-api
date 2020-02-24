package main.view.publicApi;

import main.Session;
import main.exceptions.AqualityParametersException;
import main.model.dto.TestDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/public/test/create-or-update")
public class PublicTestServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);

            TestDto test = mapper.mapObject(TestDto.class, requestedJson);

            validatePost(test);
            test = session.controllerFactory.getHandler(test).createOrUpdate(test);

            setResponseBody(resp, test);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private void validatePost(TestDto test) throws AqualityParametersException {
        if(test.getProject_id() == null) {
            throw new AqualityParametersException("You should specify 'project_id'!");
        }

        if(test.getId() == null && test.getName() == null) {
            throw new AqualityParametersException("You should specify 'id' or/and 'name' suite parameters!");
        }

        if(test.getSuites() == null) {
            throw new AqualityParametersException("You should specify 'suites' array with single suite like `[{id: test_suite_id}]`");
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
