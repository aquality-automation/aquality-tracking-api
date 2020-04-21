package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.BaseDto;


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
