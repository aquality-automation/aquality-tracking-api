package main.model.dto.integrations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public enum IntegrationSystemType {
    JIRA(1);

    private final int id;

    IntegrationSystemType(int id) {
        this.id = id;
    }

    public static IntegrationSystemType getType(int id) {
        return Arrays.stream(IntegrationSystemType.values())
                .filter(type -> type.id == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("No integration systems available with id '%1$s'. Available ids: [%2$s]", id, getAvailableIds())));
    }

    public static IntegrationSystemType getType(String name) {
        return Arrays.stream(IntegrationSystemType.values())
                .filter(type -> type.name().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("No integration systems available with name '%1$s'. Available names: [%2$s]", name, Arrays.toString(IntegrationSystemType.values()))));
    }

    private static String getAvailableIds() {
        return Arrays.stream(IntegrationSystemType.values()).map(type -> String.valueOf(type.id)).collect(Collectors.joining(","));
    }
}
