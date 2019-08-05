package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

@Data @EqualsAndHashCode(callSuper = true)
public class SuiteStatisticDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    private Integer id;
    private Integer projectId;
    private String name;
    private Integer developer_id;
    private Integer total_runs;
    private Integer passed;
    private Integer failed;
    private Integer app_issue;
    private Integer autotest_issue;
    private Integer resolution_na;
}
