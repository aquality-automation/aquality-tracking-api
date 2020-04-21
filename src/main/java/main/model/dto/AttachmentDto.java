package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class AttachmentDto extends BaseDto {
    @DataBaseName(name = "request_id")
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_path")
    @DataBaseInsert
    private String path;
}
