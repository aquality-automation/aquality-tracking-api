package main.view.integrations.references;

import main.Session;
import main.controllers.ControllerType;
import main.exceptions.AqualityException;
import main.model.dto.integrations.references.RefStatus;
import main.model.dto.integrations.references.ReferencesList;
import main.model.dto.integrations.systems.SystemDto;
import main.utils.integrations.IIssueProviderApi;
import main.utils.integrations.atlassian.jira.JiraHttpClient;
import main.view.BaseServlet;

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
            IIssueProviderApi issueProviderApi = connect(session, entry.getProject_id(), entry.getInt_system_id());
            List<RefStatus> refStatuses = issueProviderApi.getIssues(entry.getRefs());
            resp.getWriter().write(mapper.serialize(refStatuses));
            setJSONContentType(resp);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private IIssueProviderApi connect(Session session, int projectId, int intSystemId) throws AqualityException {
        SystemDto systemDto = new SystemDto();
        systemDto.setProject_id(projectId);
        systemDto.setId(intSystemId);
        List<SystemDto> systems = session.controllerFactory.getProjectEntityHandler(ControllerType.SYSTEM_CONTROLLER).get(systemDto);
        SystemDto system = systems.get(0);
        String url = system.getUrl();
        String username = system.getUsername();
        String password = system.getPassword();
        //TODO: implement factory to support different implementations of providers
        return new JiraHttpClient(url, username, password);
    }
}
