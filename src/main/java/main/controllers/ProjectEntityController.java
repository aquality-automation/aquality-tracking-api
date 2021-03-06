package main.controllers;

import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;
import main.model.dto.project.ProjectUserDto;
import main.model.dto.roles.GlobalRole;
import main.model.dto.roles.ProjectRole;
import main.model.dto.roles.RolePair;
import main.model.dto.roles.RoleValidator;
import main.model.dto.settings.UserDto;

import java.util.List;

public class ProjectEntityController<DTO extends BaseDto & IProjectEntity, D extends DAO<DTO>> extends ReadonlyController<DTO, D> {

    public ProjectEntityController(UserDto user, D dao) {
        super(user, dao);
    }

    @Override
    public DTO create(DTO entity) throws AqualityException {
        validateRoles(entity, getRolesPermittedToCreate());
        return dao.create(entity);
    }

    @Override
    public List<DTO> get(DTO entity) throws AqualityException {
        validateRoles(entity, getRolesPermittedToGet());
        return dao.searchAll(entity);
    }

    @Override
    public boolean delete(DTO entity) throws AqualityException {
        validateRoles(entity, getRolesPermittedToDelete());
        return dao.delete(entity);
    }

    private RolePair getRolesPermittedToGet() {
        return new RolePair(GlobalRole.ADMIN, ProjectRole.VIEWER);
    }

    private RolePair getRolesPermittedToCreate() {
        return new RolePair(GlobalRole.ADMIN, ProjectRole.ENGINEER);
    }

    protected RolePair getRolesPermittedToDelete() {
        return new RolePair(GlobalRole.ADMIN, ProjectRole.ENGINEER);
    }

    private void validateRoles(DTO entity, RolePair roles) throws AqualityException {
        GlobalRole globalRole = roles.getGlobal();
        ProjectRole projectRole = roles.getProject();
        ProjectUserDto projectUser = baseUser.getProjectUser(entity.getProjectId());
        if (!(RoleValidator.atLeastInRole(globalRole, baseUser) || RoleValidator.atLeastInRole(projectRole, projectUser))) {
            throw new AqualityPermissionsException(String.format("You should be global '%1$s' or at least project '%2$s' to perform this action",
                    globalRole.getName(), projectRole.getName()),
                    baseUser);
        }
    }
}
