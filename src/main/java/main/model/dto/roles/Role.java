package main.model.dto.roles;

import main.model.dto.BaseDto;

import java.util.function.Function;

public class Role<DTO extends BaseDto> {

    private final Function<DTO, Boolean> checkRole;
    private final int priority;
    private final String name;

    Role(Function<DTO, Boolean> checkRole, int priority, String name) {
        this.checkRole = checkRole;
        this.priority = priority;
        this.name = name;
    }

    int getPriority() {
        return priority;
    }

    Function<DTO, Boolean> getCheckRole() {
        return checkRole;
    }

    public String getName() {
        return name;
    }
}
