package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalProp {
    private long globalID;
    private long projectID;
    private long issueType;
    private long resolutionID;
    private String title;
    private String expression;

    @JsonProperty("global_id")
    public long getGlobalID() { return globalID; }
    @JsonProperty("global_id")
    public void setGlobalID(long value) { this.globalID = value; }

    @JsonProperty("project_id")
    public long getProjectID() { return projectID; }
    @JsonProperty("project_id")
    public void setProjectID(long value) { this.projectID = value; }

    @JsonProperty("issueType")
    public long getIssueType() { return issueType; }
    @JsonProperty("issueType")
    public void setIssueType(long value) { this.issueType = value; }

    @JsonProperty("resolution_id")
    public long getResolutionID() { return resolutionID; }
    @JsonProperty("resolution_id")
    public void setResolutionID(long value) { this.resolutionID = value; }

    @JsonProperty("title")
    public String getTitle() { return title; }
    @JsonProperty("title")
    public void setTitle(String value) { this.title = value; }

    @JsonProperty("expression")
    public String getExpression() { return expression; }
    @JsonProperty("expression")
    public void setExpression(String value) { this.expression = value; }
}