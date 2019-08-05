package main.view.Audits;

import main.Session;
import main.model.dto.AuditDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/audit/create")
public class CreateAuditServlet extends BaseServlet implements IPost {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            AuditDto audit = mapper.mapObject(AuditDto.class, requestedJson);
            audit  = session.getAuditController().create(audit);
            resp.getWriter().write(mapper.serialize(audit));
            setJSONContentType(resp);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
    }
}
