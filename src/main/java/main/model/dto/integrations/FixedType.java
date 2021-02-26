package main.model.dto.integrations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class FixedType {

    private final int id;
    private final String name;

    public FixedType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return String.format("{id:%1$s, name:%2$s}", id, name);
    }

    public static FixedType getType(List<FixedType> types, int id){
        return types.stream()
                .filter(type -> type.id == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("No type found by id %1$s. Available types: %2$s",
                        id, types.stream().map(FixedType::toString).collect(Collectors.joining(",")))));
    }

}
