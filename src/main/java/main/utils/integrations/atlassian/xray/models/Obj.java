
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
    "statusbar",
    "test"
})
public class Obj {

    @JsonProperty("statusbar")
    private Statusbar statusbar;
    @JsonProperty("test")
    private Test test;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("statusbar")
    public Statusbar getStatusbar() {
        return statusbar;
    }

    @JsonProperty("statusbar")
    public void setStatusbar(Statusbar statusbar) {
        this.statusbar = statusbar;
    }

    @JsonProperty("test")
    public Test getTest() {
        return test;
    }

    @JsonProperty("test")
    public void setTest(Test test) {
        this.test = test;
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
