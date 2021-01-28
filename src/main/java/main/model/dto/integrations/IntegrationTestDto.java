package main.model.dto.integrations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegrationTestDto extends BaseDto {
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_id")
    private Integer id;
    @DataBaseSearchable
    @DataBaseName(name = "request_key")
    @DataBaseInsert
    private String key;
    @DataBaseSearchable
    @DataBaseName(name = "request_test_id")
    @DataBaseInsert
    private Integer test_id;
    @DataBaseSearchable
    @DataBaseName(name = "request_project_id")
    @DataBaseInsert
    private Integer project_id;
    @DataBaseSearchable
    @DataBaseName(name = "request_integration_system_id")
    @DataBaseInsert
    private Integer integration_system_id;
}
