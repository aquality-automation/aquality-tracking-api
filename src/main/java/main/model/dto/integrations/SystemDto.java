package main.model.dto.integrations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.IProjectEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemDto extends BaseDto implements IProjectEntity {
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_id")
    private Integer id;
    @DataBaseSearchable
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;
    @DataBaseSearchable
    @DataBaseName(name = "request_url")
    @DataBaseInsert
    private String url;
    @DataBaseSearchable
    @DataBaseName(name = "request_username")
    @DataBaseInsert
    private String username;
    @DataBaseSearchable
    @DataBaseName(name = "request_password")
    @DataBaseInsert
    private String password;
    @DataBaseSearchable
    @DataBaseName(name = "request_api_token")
    @DataBaseInsert
    private String api_token;
    @DataBaseSearchable
    @DataBaseName(name = "request_int_system_type")
    @DataBaseInsert
    private Integer int_system_type;
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;

    @Override
    public int getProjectId() {
        return project_id;
    }
}
