package main.model.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class StepResultDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_project_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_name")
    @DataBaseInsert
    private String name;
    @DataBaseName(name="request_result_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer result_id;
    @DataBaseName(name="request_type_id")
    @DataBaseInsert
    private Integer type_id;
    @DataBaseName(name="request_final_result_id")
    @DataBaseInsert
    private Integer final_result_id;
    @DataBaseName(name="request_log")
    @DataBaseInsert
    private String log;
    @DataBaseName(name="request_order")
    @DataBaseInsert
    private Integer order;
    @DataBaseName(name="request_start_time")
    @DataBaseInsert
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date start_time;
    @DataBaseName(name="request_finish_time")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date finish_time;
    @DataBaseName(name="request_comment")
    @DataBaseInsert
    private String comment;
    @DataBaseName(name="request_attachment")
    @DataBaseInsert
    private String attachment;
}
