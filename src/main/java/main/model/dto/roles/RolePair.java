package main.model.dto.roles;

public class RolePair {

    private final GlobalRole globalRole;
    private final ProjectRole projectRole;

    public RolePair(GlobalRole globalRole, ProjectRole projectRole) {
        this.globalRole = globalRole;
        this.projectRole = projectRole;
    }

    public GlobalRole getGlobal() {
        return globalRole;
    }

    public ProjectRole getProject() {
        return projectRole;
    }
}
