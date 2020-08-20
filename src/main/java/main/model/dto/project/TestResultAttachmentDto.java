package main.model.dto.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.model.dto.AttachmentDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestResultAttachmentDto extends AttachmentDto {
    @DataBaseName(name = "request_test_result_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer test_result_id;

    @DataBaseName(name = "request_project_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer project_id;

    @DataBaseName(name = "request_test_run_id")
    @DataBaseSearchable
    private Integer test_run_id;

    private String url;

    private byte[] attachment;

    private String mimeType;
}
