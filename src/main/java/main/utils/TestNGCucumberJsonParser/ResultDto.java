package main.utils.TestNGCucumberJsonParser;

import lombok.Data;

@Data
public class ResultDto {
    private Long duration;
    private String status;
    private String error_message;
}
