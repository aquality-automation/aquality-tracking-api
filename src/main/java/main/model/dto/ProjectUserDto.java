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
        return admin > 0;
    }

    public boolean isManager(){
        return manager > 0;
    }

    public boolean isEngineer(){
        return engineer > 0;
    }

    public boolean isViewer(){
        return viewer > 0;
    }

    public boolean isEditor() {
        return admin > 0 || manager > 0 || engineer > 0;
    }
}
