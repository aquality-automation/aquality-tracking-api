package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

@Data
@EqualsAndHashCode(callSuper = true)
public class Step2TestDto extends BaseDto {
    @DataBaseName(name="request_step_id")
    @DataBaseID
    @DataBaseInsert
    private Integer step_id;
    @DataBaseName(name="request_test_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer test_id;
    @DataBaseName(name="request_order")
    @DataBaseInsert
    private Integer order;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    @DataBaseID
    private Integer project_id;

}
