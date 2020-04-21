package main.model.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.model.dto.BaseDto;
import main.model.dto.customer.CustomerDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class ProjectDto extends BaseDto {
    @DataBaseName(name = "request_name")
    @DataBaseSearchable
    @DataBaseInsert
    private String name;
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    @DataBaseName(name = "request_customer_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer customer_id;
    private CustomerDto customer;
    @DataBaseName(name = "request_user_id")
    @DataBaseSearchable
    private Integer user_id;
    @DataBaseName(name = "request_steps")
    @DataBaseInsert
    private Integer steps;
    @DataBaseName(name = "request_compare_result_pattern")
    @DataBaseInsert
    private String compare_result_pattern;
    @DataBaseName(name = "request_stability_count")
    @DataBaseInsert
    private Integer stability_count;
}
