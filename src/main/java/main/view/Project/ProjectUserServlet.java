package main.view.Project;


import main.Session;
import main.model.dto.project.ProjectUserDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users/permissions")
public class ProjectUserServlet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String result;
            if(req.getParameterMap().containsKey(PROJECT_ID_KEY)&& !req.getParameter(PROJECT_ID_KEY).isEmpty()){
                result = mapper.serialize(session.getProjectPermissions(getProjectId(req)));
            }else{
                result = mapper.serialize(session.getProjectPermissions());
            }
            setJSONContentType(resp);
            resp.getWriter().write(result);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            ProjectUserDto projectUser = mapper.mapObject(ProjectUserDto.class, getRequestJson(req));
            projectUser = session.controllerFactory.getHandler(projectUser).create(projectUser);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(projectUser));
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
            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(getProjectId(req));
            projectUserDto.setUser_id(Integer.parseInt(req.getParameter("userId")));
            session.controllerFactory.getHandler(projectUserDto).delete(projectUserDto);
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
