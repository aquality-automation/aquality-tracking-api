package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class MilestoneDto extends BaseDto{
    @DataBaseSearchable
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseSearchable
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;
}
