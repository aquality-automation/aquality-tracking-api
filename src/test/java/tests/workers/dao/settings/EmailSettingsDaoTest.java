package tests.workers.dao.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.settings.EmailSettingsDao;
import main.model.dto.audit.AuditAttachmentDto;
import main.model.dto.settings.EmailSettingsDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class EmailSettingsDaoTest extends EmailSettingsDao implements IDaoTest {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<AuditAttachmentDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws AqualityException {
        resultList.add(new AuditAttachmentDto());
        resultList.add(new AuditAttachmentDto());
        List<EmailSettingsDto> result = searchAll(new EmailSettingsDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new AuditAttachmentDto());
        create(new EmailSettingsDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+EmailSettingsDao.+")
    public void removeTest() throws AqualityException {
        delete(new EmailSettingsDto());
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
