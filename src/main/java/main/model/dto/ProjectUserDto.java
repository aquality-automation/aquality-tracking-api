package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class ProjectUserDto extends BaseDto {
    @DataBaseName(name="request_user_id")
    @DataBaseInsert
    @DataBaseSearchable
    @DataBaseID
    private Integer user_id;
    private UserDto user;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    @DataBaseID
    private Integer project_id;
    @DataBaseName(name="request_admin")
    @DataBaseInsert
    private Integer admin;
    @DataBaseName(name="request_manager")
    @DataBaseInsert
    private Integer manager;
    @DataBaseName(name="request_engineer")
    @DataBaseInsert
    private Integer engineer;
    private Integer viewer;

    public boolean isAdmin(){
        return isPermissionTrue(admin);
    }

    public boolean isManager(){
        return isPermissionTrue(manager);
    }

    public boolean isEngineer(){
        return isPermissionTrue(engineer);
    }

    public boolean isViewer(){
        return isPermissionTrue(viewer);
    }

    public boolean isEditor() {
        return isAdmin() || isManager() || isEngineer();
    }

    private boolean isPermissionTrue(Integer permission){
        return permission != null && permission > 0;
    }
}
