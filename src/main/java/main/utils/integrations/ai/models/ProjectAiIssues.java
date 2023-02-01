package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProjectAiIssues {
    private List<Project> project;
    private List<Object> common;

    @JsonProperty("Project")
    public List<Project> getProject() {
        return project;
    }

    @JsonProperty("Project")
    public void setProject(List<Project> project) {
        this.project = project;
    }

    @JsonProperty("Common")
    public List<Object> getCommon() {
        return common;
    }

    @JsonProperty("Common")
    public void setCommon(List<Object> common) {
        this.common = common;
    }
}

