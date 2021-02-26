package main.model.dto.roles;

import main.exceptions.AqualityException;
import main.model.dto.BaseDto;
import main.model.dto.project.ProjectUserDto;
import main.model.dto.settings.UserDto;

import java.util.Arrays;
import java.util.List;

public class RoleValidator {

    public static <DTO extends BaseDto> boolean atLeastInRole(Role<DTO> minimalRole, DTO user) throws AqualityException {
        for (Role<DTO> role : getRoles(user)) {
            if (minimalRole.getCheckRole().apply(user) && role.getPriority() >= minimalRole.getPriority()) {
                return true;
            }
        }
        return false;
    }

    private static <DTO extends BaseDto> List<Role> getRoles(DTO user) throws AqualityException {
        if (user instanceof UserDto) {
            return Arrays.asList(GlobalRole.ADMIN, GlobalRole.COORDINATOR, GlobalRole.AUDIT_ADMIN, GlobalRole.AUDITOR);
        } else if (user instanceof ProjectUserDto) {
            return Arrays.asList(ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.EDITOR, ProjectRole.ENGINEER, ProjectRole.VIEWER);
        } else {
            throw new AqualityException("Cannot to define list of roles for type " + (user != null ? user.getClass() : "null"));
        }
    }
}
