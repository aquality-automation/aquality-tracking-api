package main.view.Audits;

import main.Session;
import main.exceptions.AqualityException;
import main.model.dto.audit.AuditAttachmentDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/audit/attachment")
@MultipartConfig
public class AuditAttachmentsServlet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("audit_id")) {
                AuditAttachmentDto auditAttachmentDtoTemplate  = new AuditAttachmentDto();
                auditAttachmentDtoTemplate.setAudit_id(Integer.parseInt(req.getParameter("audit_id")));
                List<AuditAttachmentDto> attachments = session.getAuditController().get(auditAttachmentDtoTemplate, getProjectId(req));
                setJSONContentType(resp);
                resp.getWriter().write(mapper.serialize(attachments));
            } else {
                resp.setStatus(400);
                throw new AqualityException("You have no specify Audit ID!");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setDeleteResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("id")) {
                AuditAttachmentDto auditAttachmentDtoTemplate = new AuditAttachmentDto();
                auditAttachmentDtoTemplate.setId(Integer.parseInt(req.getParameter("id")));
                session.getAuditController().delete(auditAttachmentDtoTemplate, getProjectId(req));
            } else {
                setAuthorizationProblem(resp);
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            setPostResponseHeaders(resp);
            req.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            setEncoding(resp);
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("audit_id")) {
                FileUtils fileUtils = new FileUtils();
                List<String> filePaths = fileUtils.doUpload(req, resp, PathUtils.createPathToBin("audits", getStringQueryParameter(req, "audit_id")));
                List<AuditAttachmentDto> listOfAttachments = new ArrayList<>();
                AuditAttachmentDto auditAttachmentDtoTemplate = new AuditAttachmentDto();
                auditAttachmentDtoTemplate.setAudit_id(Integer.parseInt(req.getParameter("audit_id")));
                for (String path : filePaths) {
                    auditAttachmentDtoTemplate.setPath(path);
                    listOfAttachments.add(auditAttachmentDtoTemplate);
                }

                session.getAuditController().createMultiply(listOfAttachments);
            } else {
                resp.setStatus(400);
                throw new AqualityException("You have no specify Audit ID!");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}


