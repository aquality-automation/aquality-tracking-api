
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
    "entityId",
    "rank",
    "parentEntityId",
    "fields",
    "attachments"
})
public class EntityValues {

    @JsonProperty("entityId")
    private Integer entityId;
    @JsonProperty("rank")
    private Integer rank;
    @JsonProperty("parentEntityId")
    private Integer parentEntityId;
    @JsonProperty("fields")
    private List<Object> fields = null;
    @JsonProperty("attachments")
    private List<Object> attachments = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("entityId")
    public Integer getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("rank")
    public Integer getRank() {
        return rank;
    }

    @JsonProperty("rank")
    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @JsonProperty("parentEntityId")
    public Integer getParentEntityId() {
        return parentEntityId;
    }

    @JsonProperty("parentEntityId")
    public void setParentEntityId(Integer parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    @JsonProperty("fields")
    public List<Object> getFields() {
        return fields;
    }

    @JsonProperty("fields")
    public void setFields(List<Object> fields) {
        this.fields = fields;
    }

    @JsonProperty("attachments")
    public List<Object> getAttachments() {
        return attachments;
    }

    @JsonProperty("attachments")
    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
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
