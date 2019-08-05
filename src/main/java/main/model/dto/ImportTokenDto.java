package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class ImportTokenDto extends BaseDto {
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name = "request_import_token")
    @DataBaseInsert
    private String import_token;
}
