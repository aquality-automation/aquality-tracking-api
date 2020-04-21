package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.BaseDto;
import main.model.dto.project.ProjectUserDto;
import main.model.dto.project.TestResultDto;
import main.model.dto.project.TestSuiteDto;

import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class TestDto extends BaseDto {
    private String internalId;
    @DataBaseName(name="request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String name;
    @DataBaseName(name="request_body")
    @DataBaseInsert
    @DataBaseSearchable
    private String body;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_manual_duration")
    @DataBaseInsert
    private Integer manual_duration;
    private List<TestSuiteDto> suites;
    @DataBaseName(name="request_developer_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer developer_id;
    private ProjectUserDto developer;
    private List<TestResultDto> results;
    @DataBaseName(name="request_test_suite_id")
    @DataBaseSearchable
    private Integer test_suite_id;
    private String resolution_colors;
    private String result_colors;
    private String result_ids;
}
