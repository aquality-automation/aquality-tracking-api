package main.model.dto;

import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;


@Data @IgnoreBaseFields @EqualsAndHashCode(callSuper = true)
public class CustomerMemberDto extends UserDto {
    @DataBaseName(name = "request_customer_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer customer_id;
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
}
