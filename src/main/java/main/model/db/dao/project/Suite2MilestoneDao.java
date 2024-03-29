package main.model.db.dao.project;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.DtoMapper;
import main.model.dto.project.Suite2MilestoneDto;
import main.model.dto.project.TestSuiteDto;
import java.util.List;

public class Suite2MilestoneDao extends DAO<Suite2MilestoneDto> {
    public Suite2MilestoneDao() {
        super(Suite2MilestoneDto.class);
        remove = "{call REMOVE_SUITE_FROM_MILESTONE(?,?)}";
    }

    public void addSuite(Suite2MilestoneDto entity) throws AqualityException {
        String sql = "{call INSERT_SUITE_TO_MILESTONE(?,?)}";
        List<Pair<String, String>> parameters = entity.getParameters();
        CallStoredProcedure(sql, parameters);
    }

    public List<TestSuiteDto> getSuites(Suite2MilestoneDto entity) throws AqualityException {
        List<Pair<String, String>> parameters = entity.getSearchParameters();
        DtoMapper<TestSuiteDto> suiteMapper = new DtoMapper<>(TestSuiteDto.class);

        String sql = "{call SELECT_MILESTONE_SUITES(?)}";
        return suiteMapper.mapObjects(CallStoredProcedure(sql, parameters).toString());
    }
}
