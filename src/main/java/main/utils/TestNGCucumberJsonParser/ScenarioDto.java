package main.utils.TestNGCucumberJsonParser;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ScenarioDto {
    private Date start_timestamp;
    private String id;
    private String name;
    private String keyword;
    private String type;
    private List<StepDto> steps;
    private List<StepDto> before;
    private List<StepDto> after;
}
