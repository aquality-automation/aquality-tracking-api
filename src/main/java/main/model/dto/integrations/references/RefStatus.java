package main.model.dto.integrations.references;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.model.dto.BaseDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class RefStatus extends BaseDto {
    private String key;
    private String status;
}
