package main.model.dto.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.model.dto.LabelDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class IssueStatusDto extends LabelDto {
}
