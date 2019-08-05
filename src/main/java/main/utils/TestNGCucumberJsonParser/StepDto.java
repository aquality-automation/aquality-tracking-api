package main.utils.TestNGCucumberJsonParser;

import lombok.Data;

@Data
public class StepDto {
    private ResultDto result;
    private String name;
    private String keyword;
    private RowDto[] rows;
}
