package main.model.dto.integrations.systems.workflow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class SystemWorkflowStatusDto extends BaseDto implements IProjectEntity {
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_id")
    private Integer id;

    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_int_system_id")
    @DataBaseInsert
    private Integer int_system_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_wf_sts_type_id")
    @DataBaseInsert
    private Integer wf_sts_type_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_name")
    @DataBaseInsert
    private String name;


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
