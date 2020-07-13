package main.model.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class TestResultDto extends BaseDto {
    private String internalTestId;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer id ;
    @DataBaseName(name="request_test_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer test_id;
    private TestDto test;
    @DataBaseName(name="request_final_result_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer final_result_id;
    private FinalResultDto final_result;
    @DataBaseName(name="request_test_run_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer test_run_id;
    @DataBaseName(name="request_debug")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer debug;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date updated;
    @DataBaseName(name="request_log")
    @DataBaseInsert
    private String log;
    @DataBaseName(name="request_start_date")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date start_date;
    @DataBaseName(name="request_finish_date")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date finish_date;
    @DataBaseName(name="request_final_result_updated")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date final_result_updated;
    @DataBaseName(name="request_fail_reason")
    @DataBaseSearchable
    @DataBaseInsert
    private String fail_reason;
    @DataBaseName(name="request_limit")
    @DataBaseSearchable
    private Integer limit;
    private Integer pending;
    @DataBaseName(name="request_fail_reason_regex")
    @DataBaseSearchable
    private String fail_reason_regex;
    private List<StepResultDto> steps;
    @DataBaseName(name="request_issue_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer issue_id;
    private IssueDto issue;
    private List<TestResultAttachmentDto> attachments;
}
