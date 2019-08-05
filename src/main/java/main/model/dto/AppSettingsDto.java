package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.*;

@Data @EqualsAndHashCode(callSuper = true)
public class AppSettingsDto extends BaseDto{
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name = "request_ldap_auth")
    @DataBaseInsert
    private Integer ldap_auth;
    @DataBaseName(name = "request_base_auth")
    @DataBaseInsert
    private Integer base_auth;
    @DataBaseName(name = "request_audits")
    @DataBaseInsert
    private Integer audits;
}
