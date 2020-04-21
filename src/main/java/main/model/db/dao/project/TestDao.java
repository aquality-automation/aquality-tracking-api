package main.model.db.dao.project;

import com.mysql.cj.core.conf.url.ConnectionUrlParser;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.project.TestDto;

import java.util.ArrayList;
import java.util.List;

public class TestDao extends DAO<TestDto> {
    public TestDao() {
        super(TestDto.class);
        select = "{call SELECT_TEST(?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST(?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST(?)}";
    }
    private String testsAffectedByIssueQuery = "{call SELECT_ISSUE_TESTS(?)}";

    public List<TestDto> getTestsAffectedByIssue(Integer issueId) throws AqualityException {
        List<ConnectionUrlParser.Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new ConnectionUrlParser.Pair<>("request_issue_id", issueId.toString()));
        return dtoMapper.mapObjects(CallStoredProcedure(testsAffectedByIssueQuery, parameters).toString());
    }
}
