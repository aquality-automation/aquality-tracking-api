package main.model.db.dao.project;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.project.TestDto;

import java.util.ArrayList;
import java.util.List;

public class TestDao extends DAO<TestDto> {
    private static final String TESTS_AFFECTED_BY_ISSUE_QUERY = "{call SELECT_ISSUE_TESTS(?)}";

    public TestDao() {
        super(TestDto.class);
        select = "{call SELECT_TEST(?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST(?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST(?)}";
    }

    public List<TestDto> getTestsAffectedByIssue(Integer issueId) throws AqualityException {
        List<Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new Pair<>("request_issue_id", issueId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(TESTS_AFFECTED_BY_ISSUE_QUERY, parameters).toString());
    }
}
