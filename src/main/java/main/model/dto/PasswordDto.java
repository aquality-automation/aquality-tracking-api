package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;

@Data @EqualsAndHashCode(callSuper = true)
public class PasswordDto extends BaseDto{
    @DataBaseName(name="request_user_id")
    @DataBaseInsert
    private Integer user_id;
    @DataBaseName(name="request_old_password")
    @DataBaseInsert
    private String old_password;
    @DataBaseName(name="request_password")
    @DataBaseInsert
    private String password;
}
