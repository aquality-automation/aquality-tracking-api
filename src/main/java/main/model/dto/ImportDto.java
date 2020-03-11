package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;

@Data
public class ImportDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name="request_testrun_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer testrun_id;
    @DataBaseName(name="request_project_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer project_id;
    @DataBaseName(name="request_finish_status")
    @DataBaseInsert
    private Integer finish_status;
    @DataBaseName(name="request_started")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date started;
    @DataBaseName(name="request_finished")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date finished;
    @DataBaseName(name="request_log")
    @DataBaseInsert
    private String log;

    public void addToLog(String log){
        setLog(String.format("%s\r\n[%s] %s", getLog(), new Date(), log));
    }
}
