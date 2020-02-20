package main.view.Project;

import main.Session;
import main.model.dto.SuiteStatisticDto;
import main.model.dto.TestSuiteDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/suite/stat")
public class TestSuiteStatServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            SuiteStatisticDto suiteStatisticDto = new SuiteStatisticDto();
            if(req.getParameter("suiteId") != null){
                suiteStatisticDto.setId(Integer.parseInt(req.getParameter("suiteId")));
            }
            suiteStatisticDto.setProjectId(Integer.parseInt(req.getParameter("projectId")));
            List<SuiteStatisticDto> suiteStatistics = session.controllerFactory.getHandler(new TestSuiteDto()).get(suiteStatisticDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(suiteStatistics));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp)  {
        setOptionsResponseHeaders(resp);
        resp.addHeader("Access-Control-Allow-Headers", "id");
    }
}
