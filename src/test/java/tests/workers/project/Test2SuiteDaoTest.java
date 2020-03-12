package tests.workers.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.Test2SuiteDao;
import main.model.dto.project.Test2SuiteDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class Test2SuiteDaoTest extends Test2SuiteDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<Test2SuiteDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new Test2SuiteDto());
        resultList.add(new Test2SuiteDto());
        List<Test2SuiteDto> result = searchAll(new Test2SuiteDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new Test2SuiteDto());
        create(new Test2SuiteDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws AqualityException {
        delete(new Test2SuiteDto());
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
