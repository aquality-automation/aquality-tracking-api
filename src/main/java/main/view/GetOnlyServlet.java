package main.view;

import main.Session;
import main.controllers.ControllerType;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class GetOnlyServlet<T extends BaseDto, D extends DAO<T>> extends BaseServlet implements IGet {

    private final ControllerType<T, D> controllerType;

    public GetOnlyServlet(ControllerType<T, D> controllerType) {
        this.controllerType = controllerType;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            T entity = controllerType.createDto();
            entity.getSearchTemplateFromRequestParameters(req);
            List<T> data = session.controllerFactory.getReadonlyHandler(controllerType).get(entity);
            resp.getWriter().write(mapper.serialize(data));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
