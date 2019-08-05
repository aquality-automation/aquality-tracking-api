package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.MilestoneDao;
import main.model.dto.MilestoneDto;
import main.model.dto.UserDto;

import java.util.List;

public class MilestoneController extends BaseController<MilestoneDto> {
    private MilestoneDao milestoneDao;

    public MilestoneController(UserDto user) {
        super(user);
        milestoneDao = new MilestoneDao();
    }

    @Override
    public MilestoneDto create(MilestoneDto template) throws RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return milestoneDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Milestones", baseUser);
        }
    }

    @Override
    public List<MilestoneDto> get(MilestoneDto template) throws  RPException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return milestoneDao.searchAll(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Milestones", baseUser);
        }
    }

    @Override
    public boolean delete(MilestoneDto template) throws  RPException {
        if(baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()){
            return milestoneDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Milestones", baseUser);
        }
    }
}
