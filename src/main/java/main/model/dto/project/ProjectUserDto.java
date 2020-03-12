package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.BaseDto;
import main.model.dto.settings.UserDto;
import main.utils.BooleanUtil;


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
        return BooleanUtil.intToBoolean(admin);
    }

    public boolean isManager(){
        return BooleanUtil.intToBoolean(manager);
    }

    public boolean isEngineer(){
        return BooleanUtil.intToBoolean(engineer);
    }

    public boolean isViewer(){
        return BooleanUtil.intToBoolean(viewer);
    }

    public boolean isEditor() {
        return isAdmin() || isManager() || isEngineer();
    }
}
