package main.view.Project;

import main.Session;
import main.model.dto.ProjectDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/project")
public class ProjectServlet extends BaseServlet implements IPost, IGet, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            ProjectDto projectTemplate = new ProjectDto();
            projectTemplate.getSearchTemplateFromRequestParameters(req);
            List<ProjectDto> projects = session.getProjectController().get(projectTemplate);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(projects));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp){
        setDeleteResponseHeaders(resp);

        try {
            assertRequiredField(req, "id");
            Session session = createSession(req);
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(Integer.parseInt(req.getParameter("id")));
            session.getProjectController().delete(projectDto);
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
            ProjectDto project = mapper.mapObject(ProjectDto.class, getRequestJson(req));
            project = session.getProjectController().create(project);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(project));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
