package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class APITokenDto extends BaseDto {
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name = "request_api_token")
    @DataBaseInsert
    private String api_token;
}
