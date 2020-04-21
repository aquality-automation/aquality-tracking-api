package main.model.dto.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseName;
import main.model.dto.BaseDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class StepTypeDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseID
    private Integer id;
    @DataBaseName(name="request_name")
    private String name;
}
