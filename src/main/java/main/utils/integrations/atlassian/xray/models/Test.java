
package main.utils.integrations.atlassian.xray.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "key",
    "testType",
    "exists",
    "rank",
    "status",
    "startedExecuting",
    "canExecute",
    "disallowedExecuteMessage",
    "transitions",
    "userColumns",
    "assignee",
    "statusBarBean",
    "entityValues",
    "hasMissingCustomFields",
    "showXEAExecutionLink",
    "executionAllowedIfRestrictedToAssignee"
})
public class Test {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("key")
    private String key;
    @JsonProperty("testType")
    private String testType;
    @JsonProperty("exists")
    private Boolean exists;
    @JsonProperty("rank")
    private Integer rank;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("startedExecuting")
    private Boolean startedExecuting;
    @JsonProperty("canExecute")
    private Boolean canExecute;
    @JsonProperty("disallowedExecuteMessage")
    private String disallowedExecuteMessage;
    @JsonProperty("transitions")
    private List<Transition> transitions = null;
    @JsonProperty("userColumns")
    private UserColumns userColumns;
    @JsonProperty("assignee")
    private Object assignee;
    @JsonProperty("statusBarBean")
    private Object statusBarBean;
    @JsonProperty("entityValues")
    private EntityValues entityValues;
    @JsonProperty("hasMissingCustomFields")
    private Boolean hasMissingCustomFields;
    @JsonProperty("showXEAExecutionLink")
    private Boolean showXEAExecutionLink;
    @JsonProperty("executionAllowedIfRestrictedToAssignee")
    private Boolean executionAllowedIfRestrictedToAssignee;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("testType")
    public String getTestType() {
        return testType;
    }

    @JsonProperty("testType")
    public void setTestType(String testType) {
        this.testType = testType;
    }

    @JsonProperty("exists")
    public Boolean getExists() {
        return exists;
    }

    @JsonProperty("exists")
    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    @JsonProperty("rank")
    public Integer getRank() {
        return rank;
    }

    @JsonProperty("rank")
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("startedExecuting")
    public Boolean getStartedExecuting() {
        return startedExecuting;
    }

    @JsonProperty("startedExecuting")
    public void setStartedExecuting(Boolean startedExecuting) {
        this.startedExecuting = startedExecuting;
    }

    @JsonProperty("canExecute")
    public Boolean getCanExecute() {
        return canExecute;
    }

    @JsonProperty("canExecute")
    public void setCanExecute(Boolean canExecute) {
        this.canExecute = canExecute;
    }

    @JsonProperty("disallowedExecuteMessage")
    public String getDisallowedExecuteMessage() {
        return disallowedExecuteMessage;
    }

    @JsonProperty("disallowedExecuteMessage")
    public void setDisallowedExecuteMessage(String disallowedExecuteMessage) {
        this.disallowedExecuteMessage = disallowedExecuteMessage;
    }

    @JsonProperty("transitions")
    public List<Transition> getTransitions() {
        return transitions;
    }

    @JsonProperty("transitions")
    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    @JsonProperty("userColumns")
    public UserColumns getUserColumns() {
        return userColumns;
    }

    @JsonProperty("userColumns")
    public void setUserColumns(UserColumns userColumns) {
        this.userColumns = userColumns;
    }

    @JsonProperty("assignee")
    public Object getAssignee() {
        return assignee;
    }

    @JsonProperty("assignee")
    public void setAssignee(Object assignee) {
        this.assignee = assignee;
    }

    @JsonProperty("statusBarBean")
    public Object getStatusBarBean() {
        return statusBarBean;
    }

    @JsonProperty("statusBarBean")
    public void setStatusBarBean(Object statusBarBean) {
        this.statusBarBean = statusBarBean;
    }

    @JsonProperty("entityValues")
    public EntityValues getEntityValues() {
        return entityValues;
    }

    @JsonProperty("entityValues")
    public void setEntityValues(EntityValues entityValues) {
        this.entityValues = entityValues;
    }

    @JsonProperty("hasMissingCustomFields")
    public Boolean getHasMissingCustomFields() {
        return hasMissingCustomFields;
    }

    @JsonProperty("hasMissingCustomFields")
    public void setHasMissingCustomFields(Boolean hasMissingCustomFields) {
        this.hasMissingCustomFields = hasMissingCustomFields;
    }

    @JsonProperty("showXEAExecutionLink")
    public Boolean getShowXEAExecutionLink() {
        return showXEAExecutionLink;
    }

    @JsonProperty("showXEAExecutionLink")
    public void setShowXEAExecutionLink(Boolean showXEAExecutionLink) {
        this.showXEAExecutionLink = showXEAExecutionLink;
    }

    @JsonProperty("executionAllowedIfRestrictedToAssignee")
    public Boolean getExecutionAllowedIfRestrictedToAssignee() {
        return executionAllowedIfRestrictedToAssignee;
    }

    @JsonProperty("executionAllowedIfRestrictedToAssignee")
    public void setExecutionAllowedIfRestrictedToAssignee(Boolean executionAllowedIfRestrictedToAssignee) {
        this.executionAllowedIfRestrictedToAssignee = executionAllowedIfRestrictedToAssignee;
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
