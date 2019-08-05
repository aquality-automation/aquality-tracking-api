package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class CustomerDto extends BaseDto{
    @DataBaseSearchable
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;
    @DataBaseName(name = "request_coordinator_id")
    @DataBaseInsert
    private Integer coordinator_id = 0;
    private UserDto coordinator;
    @DataBaseName(name = "request_accounting")
    @DataBaseInsert
    private Integer accounting = 0;
    @DataBaseName(name = "request_account_manager_id")
    @DataBaseInsert
    private Integer account_manager_id;
    private UserDto account_manager;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    private List<CustomerCommentDto> comments;
    private List<CustomerAttachmentDto> attachments;
    private List<ProjectDto> projects;
    private Integer projects_count;
    private List<? extends UserDto> account_team;
}
