package main.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import main.exceptions.AqualityException;
import main.exceptions.AqualityParametersException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DtoMapperGeneral{
    private ObjectMapper mapper = new ObjectMapper();

    public DtoMapperGeneral() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public String serialize(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public <T extends BaseDto> T mapObject(Class<T> clazz, String objectJsonString) throws AqualityParametersException {
        try {
            return mapper.readValue(objectJsonString, clazz);
        } catch (IOException e) {
            throw new AqualityParametersException("qwe Cannot map Object to " + clazz.getName());
        }
    }

    public <T> List<T> mapObjects(Class<T> clazz, String arrayJsonString) throws AqualityParametersException {
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        try {
            return mapper.readValue(arrayJsonString, listType);
        } catch (IOException e) {
            throw new AqualityParametersException("qwe Cannot map Object to " + clazz.getName());
        }
    }
}
