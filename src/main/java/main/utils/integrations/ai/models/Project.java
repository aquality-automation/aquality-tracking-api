package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {
    private Long globalID;
    private Long projectID;
    private Long issueType;
    private Long resolutionID;
    private String title;
    private String expression;

    @JsonProperty("global_id")
    public Long getGlobalID() {
        return globalID;
    }

    @JsonProperty("global_id")
    public void setGlobalID(Long globalID) {
        this.globalID = globalID;
    }

    @JsonProperty("project_id")
    public Long getProjectID() {
        return projectID;
    }

    @JsonProperty("project_id")
    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    @JsonProperty("issueType")
    public Long getIssueType() {
        return issueType;
    }

    @JsonProperty("issueType")
    public void setIssueType(Long issueType) {
        this.issueType = issueType;
    }

    @JsonProperty("resolution_id")
    public Long getResolutionID() {
        return resolutionID;
    }

    @JsonProperty("resolution_id")
    public void setResolutionID(Long resolutionID) {
        this.resolutionID = resolutionID;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("expression")
    public String getExpression() {
        return expression;
    }

    @JsonProperty("expression")
    public void setExpression(String expression) {
        this.expression = expression;
    }
}
