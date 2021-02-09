package main.model.dto.integrations.tts;

public enum TestTrackingType {
    XRAY(1);

    private final int id;

    TestTrackingType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
