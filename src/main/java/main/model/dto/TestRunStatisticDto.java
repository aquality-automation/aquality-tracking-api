package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class TestRunStatisticDto extends BaseDto {
    @DataBaseName(name="request_testrun_id")
    @DataBaseSearchable
    private Integer id;
    private String build_name;
    private Integer milestone_id;
    @DataBaseName(name="request_test_suite_id")
    @DataBaseSearchable
    private Integer test_suite_id;
    @DataBaseName(name="request_project_id")
    @DataBaseSearchable
    private Integer project_id;
    private String execution_environment;
    @DataBaseName(name="request_debug")
    @DataBaseSearchable
    private Integer debug;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date start_time;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date finish_time;
    private Integer failed;
    private Integer passed;
    private Integer not_executed;
    private Integer in_progress;
    private Integer pending;
    private Integer total;
    private Integer app_issue;
    private Integer warning;
    private Integer not_assigned;
    private Integer other;
}
