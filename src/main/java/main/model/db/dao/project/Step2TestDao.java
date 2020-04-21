package main.model.db.dao.project;

import com.mysql.cj.core.conf.url.ConnectionUrlParser;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.DtoMapper;
import main.model.dto.project.Step2TestDto;
import main.model.dto.project.StepDto;

import java.util.List;

public class Step2TestDao extends DAO<Step2TestDto> {
    public Step2TestDao() {
        super(Step2TestDto.class);
        insert = "{call INSERT_STEP_TO_TEST(?,?,?,?,?)}";
        select = "{call SELECT_TEST_STEPS(?,?)}";
        remove = "{call REMOVE_STEP_FROM_TEST(?,?)}";
    }

    /**
     * Create Entity
     * @param entity entity to create
     * @return created entity
     */
    public Step2TestDto create(Step2TestDto entity) throws AqualityException {
        List<ConnectionUrlParser.Pair<String, String>> parameters = entity.getParameters();
        checkInsertProcedure();
        CallStoredProcedure(insert, parameters);
        return entity;
    }

    /**
     * Create Entity
     * @param entity entity to get
     * @return created entity
     */
    public List<StepDto> getTestSteps(Step2TestDto entity) throws AqualityException {
        List<ConnectionUrlParser.Pair<String, String>> parameters = entity.getSearchParameters();
        checkSelectProcedure();
        List<StepDto> results = new DtoMapper<>(StepDto.class).mapObjects(CallStoredProcedure(select, parameters).toString());
        return results;
    }

}
