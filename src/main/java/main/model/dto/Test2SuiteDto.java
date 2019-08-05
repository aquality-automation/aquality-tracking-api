package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class Test2SuiteDto extends BaseDto {
    @DataBaseName(name="request_test_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer test_id;
    @DataBaseName(name="request_suite_id")
    @DataBaseID
    @DataBaseInsert
    @DataBaseSearchable
    private Integer suite_id;
}
