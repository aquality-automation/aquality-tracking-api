package main.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import main.exceptions.RPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DtoMapperGeneral{
    private ObjectMapper mapper = new ObjectMapper();

    public DtoMapperGeneral() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public String serialize(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public <T extends BaseDto> T mapObject(Class<T> clazz, String objectJsonString) throws IOException {
        return mapper.readValue(objectJsonString, clazz);
    }

    public <T> List<T> mapObjects(Class<T> clazz, String arrayJsonString) throws RPException {
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        try {
            return mapper.readValue(arrayJsonString, listType);
        } catch (IOException e) {
            throw new RPException("Cannot map Object to " + clazz.getName());
        }
    }
}
