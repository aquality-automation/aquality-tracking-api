package main.model.dto;

public enum DtoFields {
    PROJECT_ID("project_id"),
    ID("id");

    private final String fieldName;

    DtoFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDbRequestName() {
        return "request_" + fieldName;
    }
}
