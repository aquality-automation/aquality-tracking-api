package tests.workers.dao.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.settings.AppSettingsDao;
import main.model.dto.AppSettingsDto;
import main.model.dto.AuditAttachmentDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tests.workers.dao.IDaoTest;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class AppSettingsDaoTest extends AppSettingsDao implements IDaoTest {

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
        List<AppSettingsDto> result = searchAll(new AppSettingsDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws AqualityException {
        resultList.add(new AuditAttachmentDto());
        create(new AppSettingsDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test(expectedExceptions = AqualityException.class, expectedExceptionsMessageRegExp = "SQL procedure 'REMOVE' is not define for DAO.+AppSettingsDao.+")
    public void removeTest() throws AqualityException {
        delete(new AppSettingsDto());
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
