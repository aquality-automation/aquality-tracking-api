package main.model.dto;

import lombok.Data;
import main.annotations.*;

@Data
public class UserSessionDto extends BaseDto {
    @DataBaseName(name="request_user_id")
    @DataBaseInsert
    private Integer user_id;
    @DataBaseName(name="request_session_code")
    @DataBaseInsert
    private String session_code;
}
