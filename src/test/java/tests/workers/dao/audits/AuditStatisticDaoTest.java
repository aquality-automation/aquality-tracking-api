package tests.workers.dao.audits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.audit.AuditStatisticDao;
import main.model.dto.AuditStatisticDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static utils.Validations.assertSQLToParams;

public class AuditStatisticDaoTest extends AuditStatisticDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<AuditStatisticDto> resultList;

    @BeforeMethod
    public void cleanUpResults() {
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new AuditStatisticDto());
        resultList.add(new AuditStatisticDto());
        List<AuditStatisticDto> result = searchAll(new AuditStatisticDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'INSERT' is not define for DAO.+AuditStatisticDao.+")
    public void insertTest() throws AqualityException {
        create(new AuditStatisticDto());
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+AuditStatisticDao.+")
    public void removeTest() throws AqualityException {
        delete(new AuditStatisticDto());
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
