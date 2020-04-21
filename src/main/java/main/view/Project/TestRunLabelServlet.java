package main.view.Project;

import main.Session;
import main.model.dto.project.TestRunDto;
import main.model.dto.project.TestRunLabelDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/testrun/labels")
public class TestRunLabelServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            TestRunLabelDto label = new TestRunLabelDto();
            if (req.getParameterMap().containsKey("id")) {
                label.setId(Integer.parseInt(req.getParameter("id")));
            }
            List<TestRunLabelDto> testRunLabels = session.controllerFactory.getHandler(new TestRunDto()).get(label);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(testRunLabels));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
