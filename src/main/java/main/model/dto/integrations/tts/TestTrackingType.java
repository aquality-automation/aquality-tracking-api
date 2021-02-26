package main.model.dto.integrations.tts;

import main.model.dto.integrations.FixedType;
import main.model.dto.integrations.IFixedType;

public enum TestTrackingType implements IFixedType {
    XRAY(1, "XRAY");

    private final FixedType type;

    TestTrackingType(int id, String name) {
        this.type = new FixedType(id, name);
    }

    @Override
    public FixedType getType() {
        return type;
    }
}
