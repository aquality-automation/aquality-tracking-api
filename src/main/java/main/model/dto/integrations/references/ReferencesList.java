package main.model.dto.integrations.references;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.model.dto.BaseDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReferencesList extends BaseDto {
    private Integer project_id;
    private Integer int_system_id;
    private List<String> refs;
}
