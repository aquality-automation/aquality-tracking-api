package main.view.Audits;

import main.Session;
import main.model.dto.audit.AuditDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/audit")
public class AuditServlet extends BaseServlet implements IDelete, IGet, IPost {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            AuditDto audit = new AuditDto();
            audit.getSearchTemplateFromRequestParameters(req);
            List<AuditDto> audits = session.getAuditController().get(audit);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(audits));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

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
    public void doDelete(HttpServletRequest req, HttpServletResponse resp){
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            AuditDto auditDto = new AuditDto();
            auditDto.setId(Integer.parseInt(req.getParameter("id")));
            session.getAuditController().delete(auditDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
