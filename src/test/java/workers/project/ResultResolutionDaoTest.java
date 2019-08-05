package workers.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.project.ResultResolutionDao;
import main.model.dto.ResultResolutionDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static testUtils.Validations.assertSQLToParams;

public class ResultResolutionDaoTest extends ResultResolutionDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<ResultResolutionDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws RPException {
        resultList.add(new ResultResolutionDto());
        resultList.add(new ResultResolutionDto());
        List<ResultResolutionDto> result = searchAll(new ResultResolutionDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws RPException {
        resultList.add(new ResultResolutionDto());
        create(new ResultResolutionDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws RPException {
        delete(new ResultResolutionDto());
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
