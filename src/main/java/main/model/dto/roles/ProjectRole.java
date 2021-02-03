package main.model.dto.roles;

import main.model.dto.project.ProjectUserDto;

import java.util.function.Function;

public class ProjectRole extends Role<ProjectUserDto> {

    public static final ProjectRole ADMIN = new ProjectRole(ProjectUserDto::isAdmin, 5, "ADMIN");
    public static final ProjectRole MANAGER = new ProjectRole(ProjectUserDto::isManager, 4, "MANAGER");
    public static final ProjectRole EDITOR = new ProjectRole(ProjectUserDto::isEditor, 3, "EDITOR");
    public static final ProjectRole ENGINEER = new ProjectRole(ProjectUserDto::isEngineer, 2, "ENGINEER");
    public static final ProjectRole VIEWER = new ProjectRole(ProjectUserDto::isViewer, 1, "VIEWER");

    private ProjectRole(Function<ProjectUserDto, Boolean> checkRole, int priority, String name) {
        super(checkRole, priority, name);
    }
}
