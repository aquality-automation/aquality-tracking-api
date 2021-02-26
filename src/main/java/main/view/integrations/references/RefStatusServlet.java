package main.view.integrations.references;

import main.Session;
import main.model.dto.integrations.references.RefStatus;
import main.model.dto.integrations.references.ReferencesList;
import main.model.dto.integrations.systems.SystemDto;
import main.utils.integrations.IIssueProviderApi;
import main.utils.integrations.providers.IntSystemIssueFactory;
import main.view.BaseServlet;
import main.view.integrations.SystemProvider;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/integration/references/status")
public class RefStatusServlet extends BaseServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            ReferencesList entry = mapper.mapObject(ReferencesList.class, getRequestJson(req));
            SystemDto system = SystemProvider.getSystem(session, entry.getProject_id(), entry.getInt_system_id());
            IIssueProviderApi issueProviderApi = IntSystemIssueFactory.getIssueProvider(system);
            List<RefStatus> refStatuses = issueProviderApi.getIssues(entry.getRefs());
            resp.getWriter().write(mapper.serialize(refStatuses));
            setJSONContentType(resp);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }
}
