package main.model.db.dao.project;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.project.TestResultDto;
import main.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestResultDao extends DAO<TestResultDto> {
    private static final String SYNC_SUITE_QUERY = "{call SELECT_LEGACY_RESULTS(?,?)}";
    private static final String SELECT_LATEST_RESULTS_BY_MILESTONE = "{call SELECT_LATEST_RESULTS_BY_MILESTONE(?)}";
    private static final String FINISH_TEST_RESULT_QUERY =
            "{call UPDATE_TEST_RESULT_WITH_FINAL_RESULT_ID_AND_FAIL_REASON(?,?,?,?)}";

    public TestResultDao() {
        super(TestResultDto.class);
        select = "{call SELECT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST_RESULT(?)}";
    }

    /**
     * @param suiteId suite id for search
     * @param testId test id for search
     * @return all not executed results till first executed
     * @throws AqualityException
     */
    public List<TestResultDto> selectSuiteLegacyResults(
            @NotNull Integer suiteId,
            @NotNull Integer testId) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_test_suite_id", suiteId.toString()));
        parameters.add(new Pair<>("request_test_id", testId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(SYNC_SUITE_QUERY, parameters).toString());
    }

    public List<TestResultDto> selectLatestResultsByMilestone(@NotNull Integer milestoneId) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_milestone_id", milestoneId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(SELECT_LATEST_RESULTS_BY_MILESTONE, parameters).toString());
    }

    /**
     * @param id test result id
     * @param finalResultId ID of final result - Failed: 1, Passed: 2, Not Executed: 3, Pending: 5
     * @param failReason fail reason message
     * @param finishDate test finish date
     * @return updated test result
     */
    public TestResultDto updateFinalResultIdAndFailReason(
            @NotNull Integer id,
            @NotNull Integer finalResultId,
            String failReason,
            Date finishDate) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_id", id.toString()));
        parameters.add(new Pair<>("request_final_result_id", finalResultId.toString()));
        parameters.add(new Pair<>("request_fail_reason", failReason));
        parameters.add(new Pair<>(
                "request_finish_date",
                finishDate == null ? StringUtils.EMPTY : String.valueOf(DateUtils.toUnixTime(finishDate))));

        return getSingleResult(
                dtoMapper.mapObjects(CallStoredProcedure(FINISH_TEST_RESULT_QUERY, parameters).toString()),
                id);
    }
}
