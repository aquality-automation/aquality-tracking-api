package main.model.dto.integrations.systems.workflow;

public enum SystemWorkflowStatusType {
    CLOSED(1);

    private final int id;

    SystemWorkflowStatusType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
