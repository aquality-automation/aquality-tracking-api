
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
    "id",
    "name",
    "description",
    "color",
    "versionName",
    "versionId",
    "final",
    "native"
})
public class Transition {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("color")
    private String color;
    @JsonProperty("versionName")
    private Object versionName;
    @JsonProperty("versionId")
    private Object versionId;
    @JsonProperty("final")
    private Boolean _final;
    @JsonProperty("native")
    private Boolean _native;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("color")
    public String getColor() {
        return color;
    }

    @JsonProperty("color")
    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty("versionName")
    public Object getVersionName() {
        return versionName;
    }

    @JsonProperty("versionName")
    public void setVersionName(Object versionName) {
        this.versionName = versionName;
    }

    @JsonProperty("versionId")
    public Object getVersionId() {
        return versionId;
    }

    @JsonProperty("versionId")
    public void setVersionId(Object versionId) {
        this.versionId = versionId;
    }

    @JsonProperty("final")
    public Boolean getFinal() {
        return _final;
    }

    @JsonProperty("final")
    public void setFinal(Boolean _final) {
        this._final = _final;
    }

    @JsonProperty("native")
    public Boolean getNative() {
        return _native;
    }

    @JsonProperty("native")
    public void setNative(Boolean _native) {
        this._native = _native;
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
