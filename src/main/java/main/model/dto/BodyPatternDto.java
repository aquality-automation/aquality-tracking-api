package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class BodyPatternDto extends BaseDto {
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name="request_id")
    private Integer id;
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name="request_name")
    private String name;
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name="request_project_id")
    private Integer project_id;
}
