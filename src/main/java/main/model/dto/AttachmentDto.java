package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;


@Data @EqualsAndHashCode(callSuper = true)
public class AttachmentDto extends BaseDto {
    @DataBaseName(name = "request_id")
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_path")
    @DataBaseInsert
    private String path;

    private String name;
}
