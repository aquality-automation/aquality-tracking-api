package main.controllers.Project;

import main.controllers.BaseController;
import main.controllers.Administration.UserController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.ProjectUserDao;
import main.model.dto.ProjectUserDto;
import main.model.dto.UserDto;

import java.util.List;

public class ProjectUserController extends BaseController<ProjectUserDto> {
    private ProjectUserDao projectUserDao;
    private UserController userController;

    public ProjectUserController(UserDto user) {
        super(user);
        projectUserDao = new ProjectUserDao();
        userController = new UserController(user);
    }

    @Override
    public ProjectUserDto create(ProjectUserDto template) throws RPException {
        if(baseUser.isAdmin() || baseUser.getProjectUser(template.getProject_id()).isAdmin()){
            return projectUserDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Project User", baseUser);
        }
    }

    @Override
    public List<ProjectUserDto> get(ProjectUserDto template) throws  RPException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return fillProjectUsers(projectUserDao.searchAll(template));
        }else{
            throw new RPPermissionsException("Account is not allowed to view Project Users", baseUser);
        }
    }

    @Override
    public boolean delete(ProjectUserDto template) throws  RPException {
        if(baseUser.isAdmin() || baseUser.getProjectUser(template.getProject_id()).isAdmin()){
            return projectUserDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Project User", baseUser);
        }
    }

    public List<ProjectUserDto> getProjectUserForPermissions(ProjectUserDto template) throws  RPException {
        return projectUserDao.searchAll(template);
    }

    private List<ProjectUserDto> fillProjectUsers(List<ProjectUserDto> projectUsers) throws RPException {
        for(ProjectUserDto projectUser: projectUsers){
            projectUser.setUser(new UserDto());
            projectUser.getUser().setId(projectUser.getUser_id());
            projectUser.setUser(userController.get(projectUser.getUser()).get(0));
        }
        return projectUsers;
    }
}
