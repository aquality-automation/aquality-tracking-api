package tests.workers.dao.audits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.audit.AuditStatusDao;
import main.model.dto.AuditStatusDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static utils.Validations.assertSQLToParams;

public class AuditStatusDaoTest extends AuditStatusDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<AuditStatusDto> resultList;

    @BeforeMethod
    public void cleanUpResults() {
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {

        resultList.add(new AuditStatusDto());
        resultList.add(new AuditStatusDto());
        List<AuditStatusDto> result = searchAll(new AuditStatusDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'INSERT' is not define for DAO.+AuditStatusDao.+")
    public void insertTest() throws AqualityException {
        create(new AuditStatusDto());
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+AuditStatusDao.+")
    public void removeTest() throws AqualityException {
        delete(new AuditStatusDto());
    }

    @Override
    protected JSONArray CallStoredProcedure(String sql, List<Pair<String, String>> parameters) {
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
