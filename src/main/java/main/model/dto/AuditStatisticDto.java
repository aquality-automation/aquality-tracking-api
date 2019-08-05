package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class AuditStatisticDto extends BaseDto{
    private String name;
    private Integer id;
    private Integer status_id;
    private Integer service_type_id;
    private ProjectDto project;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    private Integer result;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date last_created_due_date;
    private Integer last_submitted_id;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date last_submitted_date;
    private Integer last_created_id;
    private List<AuditorDto> auditors_last;
    private List<AuditorDto> auditors_next;
    private ServiceDto service;
    private String status_name;
}
