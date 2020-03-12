package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.IssueDao;
import main.model.db.dao.project.IssueStatusDao;
import main.model.dto.project.IssueDto;
import main.model.dto.project.IssueStatusDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class IssueController extends BaseController<IssueDto> {
    private IssueDao issueDao;
    private IssueStatusDao issueStatusDao;

    public IssueController(UserDto user) {
        super(user);
        issueDao = new IssueDao();
        issueStatusDao = new IssueStatusDao();
    }
    @Override
    public List<IssueDto> get(IssueDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isViewer()) {
            return issueDao.searchAll(entity);
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
        return  issueStatusDao.searchAll(status);
    }
}
