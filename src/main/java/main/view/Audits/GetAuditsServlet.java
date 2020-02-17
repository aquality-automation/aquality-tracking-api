package main.view.Audits;


import main.Session;
import main.model.dto.AuditDto;
import main.view.BaseServlet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/audit/get")
public class GetAuditsServlet extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            validateAndGetProjectId(req);
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            AuditDto audit = mapper.mapObject(AuditDto.class, requestedJson);
            List<AuditDto> audits = session.getAuditController().get(audit);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(audits));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
        resp.addHeader("Access-Control-Allow-Headers", "id");
    }
}
