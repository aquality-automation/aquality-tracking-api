package main.model.dto.integrations.publishing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class PubItemDto extends BaseDto implements IProjectEntity {
    @DataBaseSearchable
    @DataBaseName(name = "request_id")
    private Integer id;

    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_time")
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date time;

    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_int_system_id")
    @DataBaseInsert
    private Integer int_system_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_run_id")
    @DataBaseInsert
    private Integer run_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_run_ref")
    @DataBaseInsert
    private String run_ref;

    @DataBaseSearchable
    @DataBaseName(name = "request_result_id")
    @DataBaseInsert
    private Integer result_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_test_ref")
    @DataBaseInsert
    private String test_ref;

    @DataBaseSearchable
    @DataBaseName(name = "request_status")
    @DataBaseInsert
    private Integer status;

    @DataBaseSearchable
    @DataBaseName(name = "request_issue_ref")
    @DataBaseInsert
    private String issue_ref;

    @DataBaseSearchable
    @DataBaseName(name = "request_submission_result")
    @DataBaseInsert
    private String submission_result;

    @Override
    public int getProjectId() {
        return project_id;
    }

    @Override
    public void setProjectId(int projectId) {
        this.project_id = projectId;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setSubmissionSuccess(){
        this.submission_result = "success";
    }

    public void setSubmissionFailure(Throwable e){
        String message = e.getMessage();
        int maxLength = 99;
        message = message.length() > maxLength ? message.substring(0, maxLength) : message;
        this.submission_result = message;
    }
}
