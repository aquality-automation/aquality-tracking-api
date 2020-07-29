package main.model.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.model.dto.BaseDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class TestResultStatDto extends BaseDto {
    @DataBaseName(name="request_project_id")
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_testrun_started_from_date")
    @DataBaseSearchable
    private String testrun_started_from_date;
    @DataBaseName(name="request_testrun_started_to_date")
    @DataBaseSearchable
    private String testrun_started_to_date;
    private Integer test_run_id;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date test_run_started;
    private String name;
    private String status;
    private String resolution;
    private String issue_assignee;
    private String developer_name;
    private String issue_id;
    private String issue_title;
}
