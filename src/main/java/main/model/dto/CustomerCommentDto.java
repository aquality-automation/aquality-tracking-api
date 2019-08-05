package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @EqualsAndHashCode(callSuper = true)
public class CustomerCommentDto extends CommentDto{
    @DataBaseName(name="request_customer_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer customer_id;
}
