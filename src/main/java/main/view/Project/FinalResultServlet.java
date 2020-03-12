package main.view.Project;

import main.Session;
import main.view.BaseServlet;
import main.view.IGet;
import main.model.dto.project.FinalResultDto;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/final_result")
public class FinalResultServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            FinalResultDto finalResultDto = new FinalResultDto();
            finalResultDto.getSearchTemplateFromRequestParameters(req);
            List<FinalResultDto> finalResults = session.controllerFactory.getHandler(finalResultDto).get(finalResultDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(finalResults));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}