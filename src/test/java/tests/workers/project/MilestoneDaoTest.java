package tests.workers.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.MilestoneDao;
import main.model.dto.project.MilestoneDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class MilestoneDaoTest extends MilestoneDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<MilestoneDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new MilestoneDto());
        resultList.add(new MilestoneDto());
        List<MilestoneDto> result = searchAll(new MilestoneDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new MilestoneDto());
        create(new MilestoneDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws AqualityException {
        delete(new MilestoneDto());
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
