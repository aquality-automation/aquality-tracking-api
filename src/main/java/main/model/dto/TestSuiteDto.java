package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class TestSuiteDto extends BaseDto {
    @DataBaseName(name="request_name")
    @DataBaseInsert
    @DataBaseSearchable
    private String name;
    @DataBaseName(name="request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    private List<TestDto> tests;
}
