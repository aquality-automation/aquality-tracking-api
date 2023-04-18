package main.view.Project.issues;

import main.Session;
import main.controllers.Project.IssueController;
import main.model.dto.project.IssueDto;
import main.utils.integrations.ai.AiApi;
import main.utils.integrations.ai.models.Project;
import main.utils.integrations.ai.models.ProjectAiIssues;
import main.view.BaseServlet;
import main.view.IGet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/issues/ai")
public class IssueAiServlet extends BaseServlet implements IGet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            IssueDto issue = new IssueDto();
            issue.getSearchTemplateFromRequestParameters(req);
            ProjectAiIssues projectAiIssues = new AiApi().getIssues(issue.getProject_id());
            IssueController issueController = session.controllerFactory.getHandler(issue);
            for (int i = 0; i < projectAiIssues.getProject().size(); i++) {
                Project projectIssue = projectAiIssues.getProject().get(i);
                if(projectIssue.getResolutionID() != 0){
                    IssueDto issueDto = new IssueDto();
                    issueDto.setExpression(projectIssue.getExpression());
                    issueDto.setTitle(projectIssue.getTitle());
                    issueDto.setResolution_id(Math.toIntExact(projectIssue.getResolutionID()));
                    issueDto.setStatus_id(1);
                    issueDto.setCreator_id(session.getCurrentUser().getId());
                    issueDto.setProject_id(Math.toIntExact(projectIssue.getProjectID()));
                    issueController.create(issueDto);
                }
            }
            List<IssueDto> issues = issueController.get(issue);
            resp.getWriter().write(mapper.serialize(issues));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}
