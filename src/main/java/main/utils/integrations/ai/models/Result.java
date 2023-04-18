package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    private Integer id;
    private Long projectID;
    private Test test;
    private String log;
    private String failReason;
    private Date finishDate;

    @JsonProperty("id")
    public Integer getID() { return id; }
    @JsonProperty("id")
    public void setID(Integer value) { this.id = value; }

    @JsonProperty("projectID")
    public Long getProjectID() { return projectID; }
    @JsonProperty("projectID")
    public void setProjectID(Long value) { this.projectID = value; }

    @JsonProperty("test")
    public Test getTest() { return test; }
    @JsonProperty("test")
    public void setTest(Test value) { this.test = value; }

    @JsonProperty("log")
    public String getLog() { return log; }
    @JsonProperty("log")
    public void setLog(String value) { this.log = value; }

    @JsonProperty("fail_reason")
    public String getFailReason() { return failReason; }
    @JsonProperty("fail_reason")
    public void setFailReason(String value) { this.failReason = value; }

    @JsonProperty("finish_date")
    public Date getFinishDate() { return finishDate; }
    @JsonProperty("finish_date")
    public void setFinishDate(Date value) { this.finishDate = value; }
}