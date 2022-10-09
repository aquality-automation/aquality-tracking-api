package tests.workers.dao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.TestRunStatisticDao;
import main.model.dto.project.TestRunStatisticDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class TestRunStatisticDaoTest extends TestRunStatisticDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<TestRunStatisticDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new TestRunStatisticDto());
        resultList.add(new TestRunStatisticDto());
        List<TestRunStatisticDto> result = searchAll(new TestRunStatisticDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'INSERT' is not define for DAO.+TestRunStatisticDao.+")
    public void insertTest() throws AqualityException {
        create(new TestRunStatisticDto());
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+TestRunStatisticDao.+")
    public void removeTest() throws AqualityException {
        delete(new TestRunStatisticDto());
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
