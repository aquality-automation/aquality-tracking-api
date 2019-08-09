package tests.workers.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.project.FinalResultDao;
import main.model.dto.FinalResultDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class FinalResultDaoTest extends FinalResultDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<FinalResultDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws RPException {
        resultList.add(new FinalResultDto());
        resultList.add(new FinalResultDto());
        List<FinalResultDto> result = searchAll(new FinalResultDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws RPException {
        resultList.add(new FinalResultDto());
        create(new FinalResultDto());
        assertSQLToParams(currentSql, 0);
    }

    @Test
    public void removeTest() throws RPException {
        delete(new FinalResultDto());
        assertSQLToParams(currentSql, 0);
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
