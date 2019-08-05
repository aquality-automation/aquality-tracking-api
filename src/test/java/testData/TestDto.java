package testData;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data; import lombok.EqualsAndHashCode;
import main.annotations.*;

import main.utils.CustomerDateAndTimeDeserialize;
import main.model.dto.BaseDto;

import java.util.Date;

@Data @EqualsAndHashCode(callSuper = true)
public class TestDto extends BaseDto {
    @DataBaseName(name = "request_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer id;
    @DataBaseName(name = "request_integer")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer integerValue;
    @DataBaseName(name = "request_date")
    @DataBaseInsert
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date dateValue;
    @DataBaseName(name = "request_string")
    private String stringValue;
    private String noAnnotation;
}
