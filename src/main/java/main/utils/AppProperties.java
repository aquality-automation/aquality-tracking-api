package main.utils;

public class AppProperties extends PropertyUtils{
    public AppProperties() {
        super(AvailableProperties.APPLICATION);
    }

    public String getName(){
       return prop.getProperty("name");
    }
    public String getAiUrl(){
        return prop.getProperty("aiModuleUrl");
    }
}
