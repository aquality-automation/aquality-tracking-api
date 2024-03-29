package tests.workers.dao.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.project.TestResultAttachmentDao;
import main.model.dto.project.TestResultAttachmentDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class TestResultAttachmentDaoTest extends TestResultAttachmentDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<TestResultAttachmentDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new TestResultAttachmentDto());
        resultList.add(new TestResultAttachmentDto());
        List<TestResultAttachmentDto> result = searchAll(new TestResultAttachmentDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new TestResultAttachmentDto());
        create(new TestResultAttachmentDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws AqualityException {
        delete(new TestResultAttachmentDto());
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
