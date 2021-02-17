
package main.utils.integrations.atlassian.xray.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "summary",
    "issuekey",
    "testrun_assignee"
})
public class UserColumns {

    @JsonProperty("summary")
    private String summary;
    @JsonProperty("issuekey")
    private String issuekey;
    @JsonProperty("testrun_assignee")
    private String testrunAssignee;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty("issuekey")
    public String getIssuekey() {
        return issuekey;
    }

    @JsonProperty("issuekey")
    public void setIssuekey(String issuekey) {
        this.issuekey = issuekey;
    }

    @JsonProperty("testrun_assignee")
    public String getTestrunAssignee() {
        return testrunAssignee;
    }

    @JsonProperty("testrun_assignee")
    public void setTestrunAssignee(String testrunAssignee) {
        this.testrunAssignee = testrunAssignee;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
