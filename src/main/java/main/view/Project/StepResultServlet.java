package main.view.Project;

import main.Session;
import main.model.dto.StepResultDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/step/results")
public class StepResultServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            StepResultDto stepResult = mapper.mapObject(StepResultDto.class, requestedJson);
            stepResult = session.controllerFactory.getHandler(stepResult).create(stepResult);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(stepResult));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
