package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

@Data
@EqualsAndHashCode(callSuper = true)
public class StepResultDto extends BaseDto{
    @DataBaseName(name="request_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_step_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer step_id;
    @DataBaseName(name="request_result_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer result_id;
    @DataBaseName(name="request_final_result_id")
    @DataBaseInsert
    private Integer final_result_id;
    @DataBaseName(name="request_log")
    @DataBaseInsert
    private String log;
}
