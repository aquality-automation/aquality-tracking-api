package main.model.dto.integrations.systems;

import main.model.dto.integrations.FixedType;
import main.model.dto.integrations.IFixedType;

public enum SystemType implements IFixedType {
    JIRA(1, "JIRA");

    private final FixedType type;

    SystemType(int id, String name) {
        this.type = new FixedType(id, name);
    }

    @Override
    public FixedType getType() {
        return type;
    }
}
