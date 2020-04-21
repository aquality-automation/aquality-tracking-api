package main.model.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.BaseDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;


@Data @EqualsAndHashCode(callSuper = true)
public class MilestoneDto extends BaseDto {
    @DataBaseSearchable
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseSearchable
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;
    @DataBaseSearchable
    @DataBaseName(name = "request_active")
    @DataBaseInsert
    private Integer active;
    @DataBaseName(name = "request_due_date")
    @DataBaseInsert
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date due_date;
    private List<TestSuiteDto> suites;
}
