package main.view.integrations.references;

import main.Session;
import main.controllers.integrations.ReferenceController;
import main.model.dto.integrations.references.ReferenceDto;
import main.model.dto.integrations.references.ReferenceType;
import main.view.BaseServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReferenceServlet<DTO extends ReferenceDto> extends BaseServlet {

    private final ReferenceType<DTO> referenceType;

    ReferenceServlet(ReferenceType<DTO> referenceType) {
        this.referenceType = referenceType;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            DTO reference = mapper.mapObject(referenceType.getDtoClass(), requestedJson);
            ReferenceController<DTO> controller = session.controllerFactory.getHandler(referenceType);
            reference = controller.create(reference);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(reference));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
