package main.model.dto;

import lombok.Data;

@Data
public class ErrorDto {
    public ErrorDto (String message) {
        setMessage(message);
    }
    private String message;
}
