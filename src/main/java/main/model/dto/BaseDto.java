package main.model.dto;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.annotations.*;

import main.exceptions.AqualityException;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public abstract class BaseDto {

    public List<Pair<String, String>> getParameters() throws AqualityException {
        List<Pair<String, String>> list = new ArrayList<>();
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
            try {
                DataBaseName annotation = field.getAnnotation(DataBaseName.class);
                DataBaseInsert insertAnnotation = field.getAnnotation(DataBaseInsert.class);
                if(annotation != null && insertAnnotation != null){
                    field.setAccessible(true);
                    Object value = field.get(this);
                    Pair<String, String> pair = new Pair<>(annotation.name(), getStringValue(value));
                    list.add(pair);
                }
            } catch (IllegalAccessException e) {
                throw new AqualityException(String.format("Cannot read Field: %s", field.getName()));
            }
        }

        return list;
    }

    public List<Pair<String, String>> getSearchParameters() throws AqualityException {
        List<Pair<String, String>> list = new ArrayList<>();
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
            try {
                DataBaseName nameAnnotation = field.getAnnotation(DataBaseName.class);
                DataBaseSearchable searchAnnotation = field.getAnnotation(DataBaseSearchable.class);
                if(nameAnnotation != null && searchAnnotation != null){
                    field.setAccessible(true);
                    String value = getStringValue(field.get(this));
                    if(nameAnnotation.name().equals("request_limit") && (value.equals("0") || value.equals(""))){
                        value = "100000";
                    }
                    Pair<String, String> pair = new Pair<>(nameAnnotation.name(), value);
                    list.add(pair);
                }
            } catch (IllegalAccessException e) {
                throw new AqualityException(String.format("Cannot read Field: %s", field.getName()));
            }
        }

        return list;
    }

    public List<Pair<String, String>> getDataBaseIDParameters() throws AqualityException {
        List<Pair<String, String>> list = new ArrayList<>();
        List<Field> classFields = this.getClassFields();
        boolean hasIdAnnotation = hasIdAnnotation(DataBaseID.class);
        for (Field field: classFields) {
            if((!hasIdAnnotation && Objects.equals(field.getName(), "id")) || field.getAnnotation(DataBaseID.class) != null){
                try {
                    DataBaseName nameAnnotation = field.getAnnotation(DataBaseName.class);
                    if(nameAnnotation != null){
                        field.setAccessible(true);
                        Object value = field.get(this);
                        Pair<String, String> pair = new Pair<>(nameAnnotation.name(), getStringValue(value));
                        list.add(pair);
                    }
                } catch (IllegalAccessException e) {
                    throw new AqualityException(String.format("Cannot read Field: %s", field.getName()));
                }
            }
        }

        return list;
    }

    public List<Pair<String, String>> getIdSearchParameters(Integer id) throws AqualityException {
        List<Pair<String, String>> list = new ArrayList<>();
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
            DataBaseName nameAnnotation = field.getAnnotation(DataBaseName.class);
            DataBaseSearchable searchAnnotation = field.getAnnotation(DataBaseSearchable.class);
            if(nameAnnotation != null && searchAnnotation != null){
                field.setAccessible(true);
                String value = "";
                if(Objects.equals(field.getName(), "id")) {
                    value = id.toString();
                }
                if(nameAnnotation.name().equals("request_limit") && (value.equals("0") || value.equals(""))){
                    value = "100000";
                }
                Pair<String, String> pair = new Pair<>(nameAnnotation.name(), value);
                list.add(pair);
            }
        }

        if(list.isEmpty()){
            throw new AqualityException("Entity has no id parameter");
        }

        return list;
    }

    public Integer getId() throws AqualityException {
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
                field.setAccessible(true);
                if(Objects.equals(field.getName(), "id")) {
                    Object value;
                    try {
                        value = field.get(this);
                    } catch (IllegalAccessException e) {
                        throw new AqualityException(String.format("Cannot read Field: %s", field.getName()));
                    }
                    return Integer.valueOf(getStringValue(value));
                }
        }

        throw new AqualityException("Entity has no id parameter");
    }

    public void getSearchTemplateFromRequestParameters(@NotNull HttpServletRequest req) throws AqualityException {
        getTemplate(req, DataBaseSearchable.class);
    }

    public void getIDTemplateFromRequestParameters(@NotNull HttpServletRequest req) throws AqualityException {
        getTemplate(req, DataBaseID.class);
    }

    private  <T extends Annotation> void getTemplate(@NotNull HttpServletRequest req, Class<T> clazz) throws AqualityException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
            try {
                T searchAnnotation = field.getAnnotation(clazz);
                if(searchAnnotation != null && parameterMap.containsKey(field.getName())){
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    field.set(this, toObject(type, parameterMap.get(field.getName())[0]));
                }
            } catch (IllegalAccessException e) {
                throw new AqualityException(String.format("Cannot read Field: %s", field.getName()));
            }
        }
    }

    private boolean hasIdAnnotation(Class<DataBaseID> dataBaseIDClass){
        List<Field> classFields = this.getClassFields();
        for (Field field: classFields) {
            if(field.getAnnotation(dataBaseIDClass) != null){
                return true;
            }
        }
        return false;
    }

    private List<Field> getClassFields(){
        List<Field> declaredFields = new ArrayList<>();
        Class<?> superclass = this.getClass();

        if(this.getClass().getAnnotation(IgnoreBaseFields.class) == null){
            while (superclass != null){
                declaredFields.addAll(Arrays.asList(superclass.getDeclaredFields()));
                superclass = superclass.getSuperclass();
            }
        }else{
            declaredFields.addAll(Arrays.asList(superclass.getDeclaredFields()));
        }

        return declaredFields;
    }

    private String getStringValue(Object value){
        if(value != null){
            if(value instanceof Date) {
                Date valueDate = (Date) value;
                return Long.toString(valueDate.getTime() / 1000);
            } else {
                return value.toString();
            }
        }

        return "";
    }

    private Object toObject( Class clazz, String value ) {
        if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
        if( Byte.class == clazz ) return Byte.parseByte( value );
        if( Short.class == clazz ) return Short.parseShort( value );
        if( Integer.class == clazz ) return Integer.parseInt( value );
        if( Long.class == clazz ) return Long.parseLong( value );
        if( Float.class == clazz ) return Float.parseFloat( value );
        if( Double.class == clazz ) return Double.parseDouble( value );
        return value;
    }
}
