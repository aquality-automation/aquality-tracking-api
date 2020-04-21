package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;

@Data @EqualsAndHashCode(callSuper = true)
public class ResultResolutionDto extends BaseDto {
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_color")
    @DataBaseInsert
    private Integer color;
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
}
