package main.model.dto.integrations.references;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReferenceDto extends BaseDto implements IProjectEntity {
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_id")
    private Integer id;
    @DataBaseSearchable
    @DataBaseName(name = "request_key")
    @DataBaseInsert
    private String key;
    @DataBaseSearchable
    @DataBaseName(name = "request_entity_id")
    @DataBaseInsert
    private Integer entity_id;
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;
    @DataBaseSearchable
    @DataBaseName(name = "request_int_system")
    @DataBaseInsert
    private Integer int_system;

    @Override
    public int getProjectId() {
        return project_id;
    }

    @Override
    public void setProjectId(int projectId) {
        this.project_id = projectId;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
