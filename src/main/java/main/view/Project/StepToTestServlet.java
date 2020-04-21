package main.view.Project;

import main.Session;
import main.model.dto.project.Step2TestDto;
import main.model.dto.project.StepDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/test/steps")
public class StepToTestServlet extends BaseServlet implements IPost, IDelete, IGet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            Step2TestDto step2Test = mapper.mapObject(Step2TestDto.class, requestedJson);
            session.controllerFactory.getHandler(new StepDto()).assignToTest(step2Test);
            resp.getWriter().write(mapper.serialize(step2Test));
            setJSONContentType(resp);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            Step2TestDto stepToTest = new Step2TestDto();
            stepToTest.getSearchTemplateFromRequestParameters(req);
            List<StepDto> steps = session.controllerFactory.getHandler(new StepDto()).getTestSteps(stepToTest);
            resp.getWriter().write(mapper.serialize(steps));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            Step2TestDto step2Test = new Step2TestDto();
            step2Test.getIDTemplateFromRequestParameters(req);
            session.controllerFactory.getHandler(new StepDto()).removeFromTest(step2Test);
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
