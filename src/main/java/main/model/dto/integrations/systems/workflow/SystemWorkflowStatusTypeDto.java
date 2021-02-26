package main.model.dto.integrations.systems.workflow;

import lombok.Data;
import main.annotations.DataBaseID;
import main.annotations.DataBaseName;
import main.model.dto.BaseDto;

@Data
public class SystemWorkflowStatusTypeDto extends BaseDto {
    @DataBaseName(name = "request_id")
    @DataBaseID
    private Integer id;
    @DataBaseName(name = "request_name")
    private String name;
}