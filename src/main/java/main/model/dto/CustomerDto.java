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
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    private List<ProjectDto> projects;
    private Integer projects_count;
}
