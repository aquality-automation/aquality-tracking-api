package main.view.integrations.publishing;

import main.Session;
import main.controllers.ControllerType;
import main.controllers.ProjectEntityController;
import main.exceptions.AqualityException;
import main.model.db.dao.integrations.PubItemDao;
import main.model.dto.integrations.publishing.PubEntry;
import main.model.dto.integrations.publishing.PubItemDto;
import main.model.dto.integrations.systems.SystemDto;
import main.utils.integrations.ITestTrackingApi;
import main.utils.integrations.providers.TestTrackingFactory;
import main.view.CrudServlet;
import main.view.integrations.SystemProvider;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/integration/publish")
public class PublishServlet extends CrudServlet<PubItemDto, PubItemDao> {

    public PublishServlet() {
        super(ControllerType.PUBLISHING_CONTROLLER);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            PubEntry entry = mapper.mapObject(PubEntry.class, getRequestJson(req));
            SystemDto system = SystemProvider.getSystem(session, entry.getProject_id(), entry.getInt_system_id());
            ITestTrackingApi trackingApi = TestTrackingFactory.getTestTracking(system);
            ProjectEntityController<PubItemDto, PubItemDao> controller = session.controllerFactory.getProjectEntityHandler(controllerType);
            List<PubItemDto> logEntries = submit(entry, trackingApi, controller);
            resp.getWriter().write(mapper.serialize(logEntries));
            setJSONContentType(resp);
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    private List<PubItemDto> submit(PubEntry entry, ITestTrackingApi trackingApi, ProjectEntityController<PubItemDto, PubItemDao> controller) throws AqualityException, IOException {
        List<PubItemDto> log = new ArrayList<>();

        List<String> testKeys = entry.getResults().stream().map(PubItemDto::getTest_ref).collect(Collectors.toList());
        trackingApi.addTestToTestExecution(entry.getRun_ref(), testKeys);

        for (PubItemDto item : entry.getResults()) {
            item.setProject_id(entry.getProject_id());
            item.setInt_system_id(entry.getInt_system_id());
            item.setRun_id(entry.getRun_id());
            item.setRun_ref(entry.getRun_ref());
            item.setTime(Date.from(Instant.now()));

            try {
                int id = trackingApi.addTestExecTestResult(item.getRun_ref(), item.getTest_ref(), item.getStatus());
                String issueRef = item.getIssue_ref();
                if (issueRef != null && !issueRef.isEmpty()) {
                    trackingApi.addDefectToTestResult(issueRef, id);
                }
                item.setSubmissionSuccess();
                log.add(controller.create(item));
            } catch (Exception e) {
                item.setSubmissionFailure(e);
                log.add(controller.create(item));
            }
        }
        return log;
    }
}
