package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true) @IgnoreBaseFields
public class AuditorDto extends UserDto {
    @DataBaseName(name="request_audit_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer audit_id;
    private Integer project_id;
    @DataBaseName(name="request_user_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer assignee_user_id;
    @DataBaseName(name="request_auditor_id")
    @DataBaseID
    private Integer auditor_id;
}
