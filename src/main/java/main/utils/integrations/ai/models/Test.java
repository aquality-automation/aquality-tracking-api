package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Test {
    private Integer id;
    private String name;

    @JsonProperty("id")
    public Integer getID() { return id; }
    @JsonProperty("id")
    public void setID(Integer value) { this.id = value; }

    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String value) { this.name = value; }
}