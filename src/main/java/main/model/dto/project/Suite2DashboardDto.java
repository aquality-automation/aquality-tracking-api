package main.model.dto.project;


import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class Suite2DashboardDto  extends BaseDto {
    @DataBaseName(name = "request_suite_id")
    @DataBaseInsert
    private Integer suite_id;
    @DataBaseName(name = "request_id")
    private Integer id;
    @DataBaseName(name = "request_dashboard_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer dashboard_id;
}
