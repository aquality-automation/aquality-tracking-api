package main.model.dto.audit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.model.dto.*;
import main.model.dto.project.ProjectDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class AuditDto extends BaseDto {
    private ProjectDto project;
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_created")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    @DataBaseName(name = "request_due_date")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date due_date;
    @DataBaseName(name = "request_started")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date started;
    @DataBaseName(name = "request_submitted")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date submitted;
    @DataBaseName(name = "request_progress_finished")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date progress_finished;
    private AuditStatusDto status;
    @DataBaseName(name = "request_result")
    @DataBaseInsert
    private Integer result;
    @DataBaseName(name = "request_summary")
    @DataBaseInsert
    private String summary;
    private List<AuditCommentDto> comments;
    private List<AuditorDto> auditors;
    private ServiceDto service;
    @DataBaseName(name = "request_status_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer status_id;
    @DataBaseName(name = "request_service_id")
    @DataBaseInsert
    private Integer service_type_id;
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
}
