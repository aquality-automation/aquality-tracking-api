package tests.workers.dao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.Suite2MilestoneDao;
import main.model.dto.project.Suite2MilestoneDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static utils.Validations.assertSQLToParams;

public class Suite2MilestoneDaoTest extends Suite2MilestoneDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<Suite2MilestoneDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'SELECT' is not define for DAO.+Suite2MilestoneDao.+")
    public void searchAllTest() throws AqualityException {
        searchAll(new Suite2MilestoneDto());
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'INSERT' is not define for DAO.+Suite2MilestoneDao.+")
    public void insertTest() throws AqualityException {
        create(new Suite2MilestoneDto());
    }

    @Test
    public void removeTest() throws AqualityException {
        delete(new Suite2MilestoneDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void addSuiteTest() throws AqualityException {
        addSuite(new Suite2MilestoneDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void getSuitesTest() throws AqualityException {
        getSuites(new Suite2MilestoneDto());
        assertSQLToParams(currentSql, currentParameters);
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
