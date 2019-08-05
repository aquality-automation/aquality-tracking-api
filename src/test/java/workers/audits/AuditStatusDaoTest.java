package workers.audits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.audit.AuditStatusDao;
import main.model.dto.AuditStatusDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static testUtils.Validations.assertSQLToParams;

public class AuditStatusDaoTest extends AuditStatusDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<AuditStatusDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws RPException {

        resultList.add(new AuditStatusDto());
        resultList.add(new AuditStatusDto());
        List<AuditStatusDto> result = searchAll(new AuditStatusDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws RPException {
        resultList.add(new AuditStatusDto());
        create(new AuditStatusDto());
        assertNull(currentSql);
    }

    @Test
    public void removeTest() throws RPException {
        delete(new AuditStatusDto());
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
