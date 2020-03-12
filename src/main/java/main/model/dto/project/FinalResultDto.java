package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.BaseDto;


@Data @EqualsAndHashCode(callSuper = true)
public class FinalResultDto extends BaseDto {
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String name;
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_color")
    @DataBaseInsert
    private Integer color;
}
