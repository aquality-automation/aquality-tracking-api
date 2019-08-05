package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class ProjectDto extends BaseDto{
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
}
