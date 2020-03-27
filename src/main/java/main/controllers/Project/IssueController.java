package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.IssueDao;
import main.model.db.dao.project.IssueStatusDao;
import main.model.db.dao.project.TestResultDao;
import main.model.db.dao.project.UserDao;
import main.model.dto.project.*;
import main.model.dto.settings.UserDto;
import main.utils.RegexpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IssueController extends BaseController<IssueDto> {
    private IssueDao issueDao;
    private IssueStatusDao issueStatusDao;
    private UserDao userDao;
    private TestResultDao testResultDao;
    private ResultResolutionController resultResolutionController;

    public IssueController(UserDto user) {
        super(user);
        issueDao = new IssueDao();
        issueStatusDao = new IssueStatusDao();
        userDao = new UserDao();
        testResultDao = new TestResultDao();
        resultResolutionController = new ResultResolutionController(user);
    }
    @Override
    public List<IssueDto> get(IssueDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isViewer()) {
            return fillIssues(issueDao.searchAll(entity));
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Issues", baseUser);
        }
    }

    @Override
    public IssueDto create(IssueDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            List<IssueDto> issues = new ArrayList<>();
            issues.add(issueDao.create(entity));
            return fillIssues(issues).get(0);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Issues", baseUser);
        }
    }

    @Override
    public boolean delete(IssueDto entity) throws AqualityException {
        return false;
    }

    public List<IssueStatusDto> get(IssueStatusDto status) throws AqualityException {
        return issueStatusDao.searchAll(status);
    }

    public void updateResultsWithIssue(IssueDto issue) throws AqualityException {
        TestResultDto resultSearchTemplate = new TestResultDto();
        resultSearchTemplate.setProject_id(issue.getProject_id());
        List<TestResultDto> results = testResultDao.searchAll(resultSearchTemplate);
        results = results.stream().filter(x -> x.getFinal_result_id() != 2 && x.getFail_reason() != null && x.getIssue_id() == null).collect(Collectors.toList());
        for (TestResultDto result : results) {
            if (RegexpUtil.match(result.getFail_reason(), issue.getExpression())) {
                result.setIssue_id(issue.getId());
                testResultDao.create(result);
            }
        }
    }

    private List<IssueDto> fillIssues(List<IssueDto> issues) throws AqualityException {
        if(issues.isEmpty()) {
            return issues;
        }

        List<UserDto> users = userDao.getAll();
        List<IssueStatusDto> issueStatuses = get(new IssueStatusDto());
        List<IssueDto> filledIssues = new ArrayList<>();

        ResultResolutionDto resultResolution = new ResultResolutionDto();
        resultResolution.setProject_id(issues.get(0).getProject_id());
        List<ResultResolutionDto> resultResolutions = resultResolutionController.get(resultResolution);

        for (IssueDto issue : issues) {
            filledIssues.add(fillIssue(issue, users, issueStatuses, resultResolutions));
        }

        return filledIssues;
    }

    private IssueDto fillIssue(IssueDto issue, List<UserDto> users, List<IssueStatusDto> issueStatuses, List<ResultResolutionDto> resultResolutions){
        UserDto assignee = users.stream().filter(x -> x.getId().equals(issue.getAssignee_id())).findFirst().orElse(null);
        UserDto creator = users.stream().filter(x -> x.getId().equals(issue.getCreator_id())).findFirst().orElse(null);
        if (assignee != null) {
            issue.setAssignee(assignee.toPublic());
        }
        if (creator != null) {
            issue.setCreator(creator.toPublic());
        }
        issue.setStatus(issueStatuses.stream().filter(x -> x.getId().equals(issue.getStatus_id())).findFirst().orElse(null));
        issue.setResolution(resultResolutions.stream().filter(x -> x.getId().equals(issue.getResolution_id())).findFirst().orElse(null));

        return issue;
    }
}
