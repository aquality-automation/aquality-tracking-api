package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.IssueDao;
import main.model.db.dao.project.IssueStatusDao;
import main.model.db.dao.project.UserDao;
import main.model.dto.project.IssueDto;
import main.model.dto.project.IssueStatusDto;
import main.model.dto.project.ResultResolutionDto;
import main.model.dto.settings.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IssueController extends BaseController<IssueDto> {
    private IssueDao issueDao;
    private IssueStatusDao issueStatusDao;
    private UserDao userDao;
    private ResultResolutionController resultResolutionController;

    public IssueController(UserDto user) {
        super(user);
        issueDao = new IssueDao();
        issueStatusDao = new IssueStatusDao();
        userDao = new UserDao();
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
            return issueDao.create(entity);
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
            UserDto assignee = users.stream().filter(x -> x.getId().equals(issue.getAssignee_id())).findFirst().orElse(null);
            UserDto creator = users.stream().filter(x -> x.getId().equals(issue.getCreator_id())).findFirst().orElse(null);
            if(assignee != null) {
                issue.setAssignee(assignee.toPublic());
            }
            if(creator != null) {
                issue.setCreator(creator.toPublic());
            }
            issue.setStatus(issueStatuses.stream().filter(x -> x.getId().equals(issue.getStatus_id())).findFirst().orElse(null));
            issue.setResolution(resultResolutions.stream().filter(x -> x.getId().equals(issue.getResolution_id())).findFirst().orElse(null));
            filledIssues.add(issue);
        }

        return filledIssues;
    }
}
