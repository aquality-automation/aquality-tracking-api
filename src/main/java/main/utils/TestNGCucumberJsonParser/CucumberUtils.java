package main.utils.TestNGCucumberJsonParser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.exceptions.RPException;

import java.io.IOException;
import java.util.List;

public class CucumberUtils {
    private String cucumberString;
    private ObjectMapper mapper = new ObjectMapper();

    public CucumberUtils(String jsonString) throws RPException {
        cucumberString = jsonString;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<FeatureDto> getFeatures() throws RPException {
        try {
            return mapper.readValue(cucumberString, new TypeReference<List<FeatureDto>>() { });
        } catch (IOException e) {
            throw new RPException("Not Able to get features from cucumber report");
        }
    }

    public FeatureDto getFeature(String json) throws RPException {
        try {
            return mapper.readValue(json, FeatureDto.class);
        } catch (IOException e) {
            throw new RPException("Not Able to get feature from cucumber report");
        }
    }
}
