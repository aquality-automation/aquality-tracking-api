package main.utils.TestNGCucumberJsonParser;

import lombok.Data;

import java.util.List;

@Data
public class FeatureDto {
    private String name;
    private List<ScenarioDto> elements;
}
