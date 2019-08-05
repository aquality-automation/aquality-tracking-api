package main.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmailDto{
    private String subject;
    private String content;
    private List<String> recipients;
}
