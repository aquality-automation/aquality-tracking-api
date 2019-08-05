package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

@Data @EqualsAndHashCode(callSuper = true)
public class LdapDto extends BaseDto {

    @DataBaseName(name="request_ldapAdServer")
    @DataBaseInsert
    private String ldapAdServer;
    @DataBaseName(name="request_ldapSearchBaseUsers")
    @DataBaseInsert
    private String ldapSearchBaseUsers;
    @DataBaseName(name="request_security_auntification")
    @DataBaseInsert
    private String security_auntification;
    @DataBaseName(name="request_userSearchFilter")
    @DataBaseInsert
    private String userSearchFilter;
    @DataBaseName(name="request_domain")
    @DataBaseInsert
    private String domain;
    @DataBaseName(name="request_mailAttribute")
    @DataBaseInsert
    private String mailAttribute;
    @DataBaseName(name="request_firstNameAttribute")
    @DataBaseInsert
    private String firstNameAttribute;
    @DataBaseName(name="request_lastNameAttribute")
    @DataBaseInsert
    private String userNameAttribute;
    @DataBaseName(name="request_userNameAttribute")
    @DataBaseInsert
    private String lastNameAttribute;
    @DataBaseName(name="request_adminUserName")
    @DataBaseInsert
    private String adminUserName;
    @DataBaseName(name="request_adminSecret")
    @DataBaseInsert
    private String adminSecret;
    @DataBaseName(name="request_id")
    @DataBaseInsert
    private Integer id;
}
