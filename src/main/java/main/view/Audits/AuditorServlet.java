package main.view.Audits;

import main.Session;
import main.model.dto.AuditorDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/audit/auditors")
public class AuditorServlet extends BaseServlet implements IPost {


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            validateAndGetProjectId(req);
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            List<AuditorDto> auditors = mapper.mapObjects(AuditorDto.class, requestedJson);
            for (AuditorDto auditorDto :  auditors) {
                auditorDto.setAudit_id(Integer.parseInt(req.getParameter("audit_id")));
            }
            session.getAuditController().updateAuditors(auditors);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
    }
}