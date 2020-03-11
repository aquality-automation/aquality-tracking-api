package tests.workers.dao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.UserSessionDao;
import main.model.dto.UserSessionDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static utils.Validations.assertSQLToParams;

public class UserSessionDaoTest extends UserSessionDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<UserSessionDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'SELECT' is not define for DAO.+UserSessionDao.+")
    public void searchAllTest() throws AqualityException {
        searchAll(new UserSessionDto());
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new UserSessionDto());
        create(new UserSessionDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+UserSessionDao.+")
    public void removeTest() throws AqualityException {
        delete(new UserSessionDto());
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
