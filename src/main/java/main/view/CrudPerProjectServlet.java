package main.view;

import main.Session;
import main.controllers.ControllerType;
import main.controllers.ProjectEntityController;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrudPerProjectServlet<T extends BaseDto & IProjectEntity, D extends DAO<T>> extends CrudServlet<T, D> {

    public CrudPerProjectServlet(ControllerType<T, D> controllerType) {
        super(controllerType);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            T entity = controllerType.createDto();
            entity.setProjectId(getProjectId(req));
            ProjectEntityController<T, D> controller = session.controllerFactory.getProjectEntityHandler(controllerType);
            controller.createTable(entity);
            setJSONContentType(resp);
            //TODO: replace with object
            resp.getWriter().write("{\"created\":true}");
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    protected boolean delete(ProjectEntityController<T, D> controller, T entity) throws AqualityException {
        return controller.deleteInProject(entity);
    }
}
