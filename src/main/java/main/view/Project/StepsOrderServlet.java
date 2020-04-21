package main.view.Project;

import main.Session;
import main.model.dto.project.Step2TestDto;
import main.model.dto.project.StepDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/test/stepsOrder")
public class StepsOrderServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            List<Step2TestDto> step2Tests = mapper.mapObjects(Step2TestDto.class, requestedJson);
            List<StepDto> steps = session.controllerFactory.getHandler(new StepDto()).updateOrder(step2Tests);
            resp.getWriter().write(mapper.serialize(steps));
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
