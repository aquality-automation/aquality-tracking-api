package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class EmailSettingsDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_host")
    @DataBaseInsert
    private String host;
    @DataBaseName(name="request_port")
    @DataBaseInsert
    private String port;
    @DataBaseName(name="request_user")
    @DataBaseInsert
    private String user;
    @DataBaseName(name="request_from_email")
    @DataBaseInsert
    private String from_email;
    @DataBaseName(name="request_password")
    @DataBaseInsert
    private String password;
    @DataBaseName(name="request_enabled")
    @DataBaseInsert
    private Integer enabled;
    @DataBaseName(name="request_use_auth")
    @DataBaseInsert
    private Integer use_auth;
}
