package main.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import main.annotations.DataBaseInsert;
import main.annotations.DataBaseName;
import main.annotations.DataBaseSearchable;
import main.exceptions.AqualityException;
import main.model.db.dao.project.TestRunDao;
import main.utils.CustomerDateAndTimeDeserialize;

import java.util.Date;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class TestRunDto extends BaseDto {
    @DataBaseName(name="request_id")
    @DataBaseSearchable
    @DataBaseInsert
    private Integer id;
    @DataBaseName(name="request_build_name")
    @DataBaseSearchable
    @DataBaseInsert
    private String build_name;
    @DataBaseName(name="request_start_time")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date start_time;
    private MilestoneDto milestone;
    @DataBaseName(name="request_milestone_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer milestone_id;
    private TestSuiteDto test_suite;
    @DataBaseName(name="request_test_suite_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer test_suite_id;
    @DataBaseName(name="request_project_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer project_id;
    @DataBaseName(name="request_execution_environment")
    @DataBaseInsert
    @DataBaseSearchable
    private String execution_environment;
    @DataBaseName(name="request_finish_time")
    @DataBaseInsert
    @JsonDeserialize(using=CustomerDateAndTimeDeserialize.class)
    private Date finish_time;
    @DataBaseName(name="request_author")
    @DataBaseInsert
    private String author;
    @DataBaseName(name="request_debug")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer debug;
    private List<TestResultDto> testResults;
    @DataBaseName(name="request_label_id")
    @DataBaseInsert
    @DataBaseSearchable
    private Integer label_id;
    private TestRunLabelDto label;
    @DataBaseName(name="request_limit")
    @DataBaseSearchable
    private Integer limit;
    @DataBaseName(name="request_ci_build")
    @DataBaseInsert
    private String ci_build;

    public Integer getProjectIdById() throws AqualityException {
        TestRunDao testRunDao = new TestRunDao();
        try {
            this.setLimit(1);
            return this.getProject_id() == null && this.getId() != null
                    ? testRunDao.searchAll(this).get(0).getProject_id()
                    : this.getProject_id();
        }catch (IndexOutOfBoundsException e){
            throw new AqualityException("You are trying to access testrun that is not present!");
        }
    }
}
