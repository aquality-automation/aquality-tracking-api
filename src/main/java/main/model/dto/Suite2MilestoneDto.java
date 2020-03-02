package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;


@Data @EqualsAndHashCode(callSuper = true)
public class Suite2MilestoneDto extends BaseDto {
    @DataBaseName(name="request_milestone_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer milestone_id;
    @DataBaseName(name="request_suite_id")
    @DataBaseID
    @DataBaseInsert
    private Integer suite_id;
}
