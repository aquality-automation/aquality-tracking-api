package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class AuditStatusDto extends BaseDto{
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer id;
    private String name;
    private Integer color;
}
