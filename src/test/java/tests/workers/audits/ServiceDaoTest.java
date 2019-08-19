package tests.workers.audits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.audit.ServiceDao;
import main.model.dto.ServiceDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static utils.Validations.assertSQLToParams;

public class ServiceDaoTest extends ServiceDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<ServiceDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new ServiceDto());
        resultList.add(new ServiceDto());
        List<ServiceDto> result = searchAll(new ServiceDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new ServiceDto());
        create(new ServiceDto());
        assertNull(currentSql);
    }

    @Test
    public void removeTest() throws AqualityException {
        delete(new ServiceDto());
        assertNull(currentSql);
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
