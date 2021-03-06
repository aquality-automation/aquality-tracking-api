package main.model.dto.settings;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.BaseDto;


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
    @DataBaseName(name="request_starttls")
    @DataBaseInsert
    private Integer starttls;
    @DataBaseName(name="request_default_email_pattern")
    @DataBaseInsert
    private String default_email_pattern;
    @DataBaseName(name="request_base_url")
    @DataBaseInsert
    private String base_url;

    public boolean isStartTlsEnabled(){
        return isPropertyEnabled(starttls);
    }

    public boolean isSmtpAuthEnabled(){
        return isPropertyEnabled(use_auth);
    }

    private boolean isPropertyEnabled(Integer property){
        return property != null && property.equals(1);
    }
}
