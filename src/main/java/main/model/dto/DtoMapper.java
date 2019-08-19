package main.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import main.exceptions.AqualityException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DtoMapper<T extends BaseDto> {
    private Class<T> type;
    private ObjectMapper mapper = new ObjectMapper();

    public DtoMapper(Class<T> type) {
        this.type = type;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public String serialize(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public T mapObject(String objectJsonString) throws IOException {
        return mapper.readValue(objectJsonString, type);
    }

    public List<T> mapObjects(String arrayJsonString) throws AqualityException {
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
        try {
            return mapper.readValue(arrayJsonString, listType);
        } catch (IOException e) {
            throw new AqualityException("Cannot map Object to " + type.getName());
        }
    }
}
