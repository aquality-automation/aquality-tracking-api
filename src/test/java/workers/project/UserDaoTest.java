package workers.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.project.UserDao;
import main.model.dto.UserDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static testUtils.Validations.assertSQLToParams;

public class UserDaoTest extends UserDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<UserDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws RPException {
        resultList.add(new UserDto());
        resultList.add(new UserDto());
        List<UserDto> result = searchAll(new UserDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws RPException {
        resultList.add(new UserDto());
        create(new UserDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws RPException {
        delete(new UserDto());
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
