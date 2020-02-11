package main.view.Project;


import main.Session;
import main.model.dto.ProjectUserDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/project/users")
public class ProjectUsersServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            ProjectUserDto projectUserDto = new ProjectUserDto();
            Integer userId = getIntegerQueryParameter(req, "userId");
            Integer projectId = getIntegerQueryParameter(req, "projectId");
            projectUserDto.setProject_id(projectId);
            projectUserDto.setUser_id(userId);
            List<ProjectUserDto> projectUsers = session.controllerFactory.getHandler(projectUserDto).get(projectUserDto);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(projectUsers));
        }catch (Exception e) {
            handleException(resp, e);
        }

    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
    }
}