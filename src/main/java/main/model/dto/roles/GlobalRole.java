package main.model.dto.roles;

import main.model.dto.settings.UserDto;

import java.util.function.Function;

public class GlobalRole extends Role<UserDto> {

    public static final GlobalRole ADMIN = new GlobalRole(UserDto::isAdmin, 5, "ADMIN");
    public static final GlobalRole COORDINATOR = new GlobalRole(UserDto::isCoordinator, 4, "COORDINATOR");
    public static final GlobalRole MANAGER = new GlobalRole(UserDto::isManager, 3, "MANAGER");
    public static final GlobalRole AUDIT_ADMIN = new GlobalRole(UserDto::isAuditAdmin, 2, "AUDIT_ADMIN");
    public static final GlobalRole AUDITOR = new GlobalRole(UserDto::isAdmin, 1, "AUDITOR");

    private GlobalRole(Function<UserDto, Boolean> checkRole, int priority, String name) {
        super(checkRole, priority, name);
    }
}
