package main.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseID;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;

@Data @EqualsAndHashCode(callSuper = true)
public class PredefinedResolutionDto extends BaseDto {
    @DataBaseID
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name="request_id")
    private Integer id;
    @DataBaseSearchable
    @DataBaseInsert
    @DataBaseName(name="request_project_id")
    private Integer project_id;
    @DataBaseInsert
    @DataBaseName(name="request_resolution_id")
    private Integer resolution_id;
    @DataBaseInsert
    @DataBaseName(name="request_comment")
    private String comment;
    @DataBaseInsert
    @DataBaseName(name="request_assignee")
    private Integer assignee;
    @DataBaseInsert
    @DataBaseName(name="request_expression")
    private String expression;
    private ResultResolutionDto resolution;
    private UserDto assigned_user;
}
