package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.model.dto.settings.UserDto;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class CommentDto extends BaseDto{
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name = "request_body")
    @DataBaseInsert
    private String body;
    private UserDto author;
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date created;
    @DataBaseName(name = "request_user_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer user_id;
}
