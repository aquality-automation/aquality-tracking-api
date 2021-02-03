package main.controllers;

import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;
import main.model.dto.IProjectEntity;
import main.model.dto.project.ProjectUserDto;
import main.model.dto.roles.GlobalRole;
import main.model.dto.roles.ProjectRole;
import main.model.dto.roles.RoleValidator;
import main.model.dto.settings.UserDto;

import java.util.List;

public class ProjectEntityController<DTO extends BaseDto & IProjectEntity, D extends DAO<DTO>> extends BaseController<DTO> {

    private final D dao;

    public ProjectEntityController(UserDto user, D dao) {
        super(user);
        this.dao = dao;
    }

    @Override
    public DTO create(DTO entity) throws AqualityException {
        validateRoles(entity, GlobalRole.ADMIN, ProjectRole.ENGINEER);
        return dao.create(entity);
    }

    @Override
    public List<DTO> get(DTO entity) throws AqualityException {
        validateRoles(entity, GlobalRole.ADMIN, ProjectRole.VIEWER);
        return dao.searchAll(entity);
    }

    @Override
    public boolean delete(DTO entity) throws AqualityException {
        validateRoles(entity, GlobalRole.ADMIN, ProjectRole.ENGINEER);
        return dao.delete(entity);
    }

    private void validateRoles(DTO entity, GlobalRole globalRole, ProjectRole projectRole) throws AqualityException {
        ProjectUserDto projectUser = baseUser.getProjectUser(entity.getProjectId());
        if (!(RoleValidator.atLeastInRole(globalRole, baseUser) || RoleValidator.atLeastInRole(projectRole, projectUser))) {
            throw new AqualityPermissionsException(String.format("You should have at least global role '%1$s' or project role '%2$s' to perform this action",
                    globalRole.getName(), projectRole.getName()),
                    baseUser);
        }
    }
}
