package main.model.dto.integrations.references;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestReferenceDto extends ReferenceDto {

    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name = "request_ref_table")
    private String ref_table = "int_test_references";
}
