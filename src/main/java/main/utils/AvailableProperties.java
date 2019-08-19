package main.utils;

public enum AvailableProperties {
    APPLICATION("appinfo.properties");

    private String name;
    AvailableProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
