
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
    "obj",
    "errors",
    "statusChanged"
})
public class AddTestResultResponse {

    @JsonProperty("obj")
    private Obj obj;
    @JsonProperty("errors")
    private List<Object> errors = null;
    @JsonProperty("statusChanged")
    private Boolean statusChanged;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("obj")
    public Obj getObj() {
        return obj;
    }

    @JsonProperty("obj")
    public void setObj(Obj obj) {
        this.obj = obj;
    }

    @JsonProperty("errors")
    public List<Object> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @JsonProperty("statusChanged")
    public Boolean getStatusChanged() {
        return statusChanged;
    }

    @JsonProperty("statusChanged")
    public void setStatusChanged(Boolean statusChanged) {
        this.statusChanged = statusChanged;
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
