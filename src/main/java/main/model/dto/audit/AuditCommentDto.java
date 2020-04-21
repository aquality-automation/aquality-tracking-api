package main.model.dto.audit;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;
import main.model.dto.CommentDto;


@Data @EqualsAndHashCode(callSuper = true)
public class AuditCommentDto extends CommentDto {
    @DataBaseName(name = "request_audit_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer audit_id;
    private Integer project_id;
}
