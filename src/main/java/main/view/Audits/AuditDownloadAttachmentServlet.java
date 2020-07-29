package main.view.Audits;

import main.Session;
import main.exceptions.AqualityException;
import main.model.dto.audit.AuditAttachmentDto;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@WebServlet("/audit/attachment/download")
@MultipartConfig
public class AuditDownloadAttachmentServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("id")) {
                AuditAttachmentDto auditAttachmentDto = new AuditAttachmentDto();
                auditAttachmentDto.setId(Integer.parseInt(req.getParameter("id")));
                List<AuditAttachmentDto> auditAttachments = session.getAuditController().get(auditAttachmentDto, getProjectId(req));
                processResponse(resp, auditAttachments.get(0).getPath());
            } else {
                resp.setStatus(400);
                throw new AqualityException("You have no specify Attachment ID");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}