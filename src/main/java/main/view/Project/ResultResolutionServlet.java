package main.view.Project;

import main.Session;
import main.model.dto.ResultResolutionDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/result_resolution")
public class ResultResolutionServlet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            Integer project_id = (req.getParameterMap().containsKey("projectId") && !req.getParameter("projectId").equals(""))
                    ? Integer.parseInt(req.getParameter("projectId"))
                    : null;
            ResultResolutionDto resultResolutionDto = new ResultResolutionDto();
            resultResolutionDto.setProject_id(project_id);
            List<ResultResolutionDto> resultResolutions = session.controllerFactory.getHandler(resultResolutionDto).get(resultResolutionDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(resultResolutions));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            ResultResolutionDto resultResolutionDto = mapper.mapObject(ResultResolutionDto.class, requestedJson);
            session.controllerFactory.getHandler(resultResolutionDto).create(resultResolutionDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            Integer project_id = (req.getParameterMap().containsKey("projectId") && !req.getParameter("projectId").equals(""))
                    ? Integer.parseInt(req.getParameter("projectId"))
                    : null;
            if(project_id != null) {
                ResultResolutionDto resultResolutionTemplate = new ResultResolutionDto();
                resultResolutionTemplate.setId(Integer.parseInt(req.getParameter("id")));
                session.controllerFactory.getHandler(resultResolutionTemplate).delete(resultResolutionTemplate);
            } else {
                resp.setStatus(401);
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
