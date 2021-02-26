package main.model.dto.integrations.tts;

import lombok.Data;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.interfaces.IProjectEntity;

@Data
public class TestTrackingStatusDto extends BaseDto implements IProjectEntity {
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
    @DataBaseName(name = "request_tts_type_id")
    @DataBaseInsert
    private Integer tts_type_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_status_name")
    @DataBaseInsert
    private String status_name;

    @DataBaseSearchable
    @DataBaseName(name = "request_status_id")
    @DataBaseInsert
    private Integer status_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_final_result_id")
    @DataBaseInsert
    private Integer final_result_id;

    @DataBaseSearchable
    @DataBaseName(name = "request_resolution_id")
    @DataBaseInsert
    private Integer resolution_id;

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