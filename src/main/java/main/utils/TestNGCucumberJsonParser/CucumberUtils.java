package main.utils.TestNGCucumberJsonParser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.exceptions.AqualityException;

import java.io.IOException;
import java.util.List;

public class CucumberUtils {
    private String cucumberString;
    private ObjectMapper mapper = new ObjectMapper();

    public CucumberUtils(String jsonString) throws AqualityException {
        cucumberString = jsonString;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<FeatureDto> getFeatures() throws AqualityException {
        try {
            return mapper.readValue(cucumberString, new TypeReference<List<FeatureDto>>() { });
        } catch (IOException e) {
            throw new AqualityException("Not Able to get features from cucumber report");
        }
    }

    public FeatureDto getFeature(String json) throws AqualityException {
        try {
            return mapper.readValue(json, FeatureDto.class);
        } catch (IOException e) {
            throw new AqualityException("Not Able to get feature from cucumber report");
        }
    }
}
