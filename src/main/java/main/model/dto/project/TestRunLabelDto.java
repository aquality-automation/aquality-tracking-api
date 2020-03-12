package main.model.dto.project;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.model.dto.LabelDto;

@Data @EqualsAndHashCode(callSuper = true)
public class TestRunLabelDto extends LabelDto {
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    private Integer project_id;
}
