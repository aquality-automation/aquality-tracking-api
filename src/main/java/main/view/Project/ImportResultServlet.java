package main.view.Project;

import main.Session;
import main.model.dto.ImportDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/import/results")
public class ImportResultServlet extends BaseServlet implements IGet{
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Integer projectId = validateAndGetProjectId(req);
            Session session = createSession(req);
            ImportDto searchTemplate = new ImportDto();
            searchTemplate.setProject_id(projectId);
            List<ImportDto> imports = session.controllerFactory.getHandler(searchTemplate).get(searchTemplate);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(imports));

        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
