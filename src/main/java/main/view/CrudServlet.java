package main.view;

import main.Session;
import main.controllers.ControllerType;
import main.controllers.ProjectEntityController;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;
import main.model.dto.DtoFields;
import main.model.dto.interfaces.IProjectEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CrudServlet<T extends BaseDto & IProjectEntity, D extends DAO<T>> extends BaseServlet implements IGet, IPost, IDelete {

    final ControllerType<T, D> controllerType;

    public CrudServlet(ControllerType<T, D> controllerType) {
        this.controllerType = controllerType;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            T entity = createAndInit(req);
            List<T> data = session.controllerFactory.getReadonlyHandler(controllerType).get(entity);
            resp.getWriter().write(mapper.serialize(data));
            setJSONContentType(resp);
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
            String requestedJson = getRequestJson(req);
            T entity = mapper.mapObject(controllerType.getDtoClass(), requestedJson);
            ProjectEntityController<T, D> controller = session.controllerFactory.getProjectEntityHandler(controllerType);
            entity = controller.create(entity);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(entity));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            T entity = createAndInit(req);
            ProjectEntityController<T, D> controller = session.controllerFactory.getProjectEntityHandler(controllerType);
            delete(controller, entity);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    protected boolean delete(ProjectEntityController<T, D> controller, T entity) throws AqualityException {
        return controller.delete(entity);
    }

    private T createAndInit(HttpServletRequest req) {
        T entity = controllerType.createDto();
        getIntegerParameter(req, DtoFields.ID).ifPresent(entity::setId);
        getIntegerParameter(req, DtoFields.PROJECT_ID).ifPresent(entity::setProjectId);
        return entity;
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
