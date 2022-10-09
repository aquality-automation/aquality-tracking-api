package main.model.db.dao.project;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.project.TestResultDto;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestResultDao extends DAO<TestResultDto> {
    public TestResultDao() {
        super(TestResultDto.class);
        select = "{call SELECT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST_RESULT(?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST_RESULT(?)}";
    }


    private String syncSuiteQuery = "{call SELECT_LEGACY_RESULTS(?,?)}";
    private String latestResultsByMilestone = "{call SELECT_LATEST_RESULTS_BY_MILESTONE(?)}";

    /**
     * @param suiteId suite id for search
     * @param testId test id for search
     * @return all not executed results till first executed
     * @throws AqualityException
     */
    public List<TestResultDto> selectSuiteLegacyResults(@NotNull Integer suiteId, @NotNull Integer testId) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_test_suite_id", suiteId.toString()));
        parameters.add(new Pair<>("request_test_id", testId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(syncSuiteQuery, parameters).toString());
    }

    public List<TestResultDto> selectLatestResultsByMilestone(@NotNull Integer milestoneId) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_milestone_id", milestoneId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(latestResultsByMilestone, parameters).toString());
    }
}
