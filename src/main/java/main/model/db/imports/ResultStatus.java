package main.model.db.imports;

public enum ResultStatus {
    FAILED(1),
    PASSED(2),
    NOT_EXECUTED(3),
    IN_PROGRESS(4),
    PENDING(5);

    private final int value;

    ResultStatus(final int statusId) {
        value = statusId;
    }

    public int getValue() { return value; }
}
