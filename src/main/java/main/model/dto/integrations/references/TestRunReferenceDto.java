package main.model.dto.integrations.references;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseRequired;
import main.annotations.DataBaseSearchable;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestRunReferenceDto extends ReferenceDto {

    @DataBaseRequired
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_ref_table")
    private String ref_table = "int_testrun_references";
}
