package tests.workers.dao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.StepTypeDao;
import main.model.dto.StepTypeDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class StepTypeDaoTest extends StepTypeDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<StepTypeDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new StepTypeDto());
        resultList.add(new StepTypeDto());
        List<StepTypeDto> result = searchAll(new StepTypeDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'INSERT' is not define for DAO.+StepTypeDao.+")
    public void insertTest() throws AqualityException {
        create(new StepTypeDto());
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+StepTypeDao.+")
    public void removeTest() throws AqualityException {
        delete(new StepTypeDto());
    }

    @Override
    protected JSONArray CallStoredProcedure(String sql, List<Pair<String, String>> parameters){
        currentSql = sql;
        currentParameters = parameters;
        try {
            return new JSONArray(dtoMapper.serialize(resultList));
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}
