package main.model.dto.audit;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.AttachmentDto;


@Data @EqualsAndHashCode(callSuper = true)
public class AuditAttachmentDto extends AttachmentDto {
    @DataBaseName(name = "request_audit_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer audit_id;
}
