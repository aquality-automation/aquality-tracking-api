package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.exceptions.AqualityException;
import main.model.db.dao.project.TestSuiteDao;

import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_first_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String first_name;
    @DataBaseName(name="request_second_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String second_name;
    @DataBaseName(name="request_user_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String user_name;
    @DataBaseName(name="request_password")
    @DataBaseInsert
    private String password;
    private String updated;
    @DataBaseName(name="request_session_code")
    private String session_code;
    private String session_created;
    @DataBaseName(name="request_admin")
    @DataBaseInsert
    private Integer admin;
    @DataBaseName(name="request_manager")
    @DataBaseInsert
    private Integer manager;
    @DataBaseName(name="request_auditor")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer auditor;
    @DataBaseName(name="request_unit_coordinator")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer unit_coordinator;
    @DataBaseName(name="request_audit_admin")
    @DataBaseInsert
    private Integer audit_admin;
    @DataBaseName(name="request_email")
    @DataBaseInsert
    private String email;
    @DataBaseName(name="request_ldap_user")
    @DataBaseInsert
    private Integer ldap_user;
    @DataBaseName(name="request_head")
    @DataBaseInsert
    private Integer head;
    @DataBaseName(name="request_account_manager")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer account_manager;
    @DataBaseName(name="request_audit_notifications")
    @DataBaseInsert
    private Integer audit_notifications;
    private List<ProjectUserDto> projectUsers;
    private Integer ApiSessionProjectId;

    public boolean isAdmin(){
        return isPermissionTrue(admin);
    }

    public boolean isCoordinator(){
        return isPermissionTrue(unit_coordinator);
    }

    public boolean isAuditor(){
        return isPermissionTrue(auditor);
    }

    public boolean isAuditAdmin(){
        return isPermissionTrue(audit_admin);
    }

    public boolean isManager(){
        return isPermissionTrue(manager);
    }


    public boolean isFromGlobalManagement(){
        return isManager() || isAuditor() || isAuditAdmin() || account_manager > 0 || isCoordinator() || head > 0;
    }

    public ProjectUserDto getProjectUser(Integer projectId){
        ProjectUserDto emptyPU = new ProjectUserDto();
        emptyPU.setViewer(0);
        emptyPU.setAdmin(0);
        emptyPU.setManager(0);
        emptyPU.setEngineer(0);
        if(projectUsers != null){
           return projectUsers.stream().filter(x -> x.getProject_id().equals(projectId)).findFirst().orElse(emptyPU);
        }
        return emptyPU;
    }

    public ProjectUserDto getProjectUserBySuiteId(Integer suite_id) throws AqualityException {
        TestSuiteDao testSuiteDao = new TestSuiteDao();
        TestSuiteDto template = new TestSuiteDto();
        template.setId(suite_id);
        template = testSuiteDao.searchAll(template).get(0);
        return getProjectUser(template.getProject_id());
    }

    public UserDto toPublic(){
        this.setPassword("");
        this.setSession_code("");
        return this;
    }

    private boolean isPermissionTrue(Integer permission){
        return permission != null && permission > 0;
    }
}
