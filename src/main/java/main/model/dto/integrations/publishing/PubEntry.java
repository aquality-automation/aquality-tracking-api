package main.model.dto.integrations.publishing;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.model.dto.BaseDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PubEntry extends BaseDto {

    private Integer project_id;
    private Integer int_system_id;
    private Integer run_id;
    private String run_ref;
    private String time;
    private List<PubItemDto> results;

}
