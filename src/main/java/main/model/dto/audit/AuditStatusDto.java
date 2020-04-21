package main.model.dto.audit;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.LabelDto;


@Data @EqualsAndHashCode(callSuper = true)
public class AuditStatusDto extends LabelDto {
}
