package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data
public class IssueDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name="request_resolution_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer resolution_id;
    @DataBaseName(name="request_title")
    @DataBaseInsert
    private String title;
    @DataBaseName(name="request_description")
    @DataBaseInsert
    private String description;
    @DataBaseName(name="request_external_url")
    @DataBaseInsert
    private String external_url;
    @DataBaseName(name="request_assignee_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer assignee_id;
    @DataBaseName(name="request_expression")
    @DataBaseInsert
    private String expression;
    @DataBaseName(name="request_status_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer status_id;
    @DataBaseName(name="request_label_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer label_id;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_creator_id")
    @DataBaseInsert
    private Integer creator_id;
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date created;
    @DataBaseName(name="request_limit")
    @DataBaseSearchable
    private Integer limit;
}
