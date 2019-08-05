package main.view.Audits;

import main.Session;
import main.model.dto.AuditDto;
import main.view.BaseServlet;
import main.view.IDelete;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/audit")
public class AuditServlet extends BaseServlet implements IDelete {

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
