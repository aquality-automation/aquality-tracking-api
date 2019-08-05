package main.view.Audits;

import main.Session;
import main.model.dto.AuditCommentDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/audit/comment")
public class AuditCommentServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        try {
            setPostResponseHeaders(resp);
            setEncoding(resp);
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            AuditCommentDto comment = mapper.mapObject(AuditCommentDto.class, requestedJson);
            session.getAuditController().create(comment);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}
