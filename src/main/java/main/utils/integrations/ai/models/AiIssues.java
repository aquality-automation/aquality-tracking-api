package main.utils.integrations.ai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AiIssues {
    private List<AdditionalProp> additionalProp;

    @JsonProperty("additionalProp")
    public List<AdditionalProp> getAdditionalProp() { return additionalProp; }
    @JsonProperty("additionalProp")
    public void setAdditionalProp(List<AdditionalProp> value) { this.additionalProp = value; }
}