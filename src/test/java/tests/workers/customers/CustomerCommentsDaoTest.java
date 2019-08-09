package tests.workers.customers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.customer.CustomerCommentsDao;
import main.model.dto.AuditAttachmentDto;
import main.model.dto.CustomerCommentDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static utils.Validations.assertSQLToParams;

public class CustomerCommentsDaoTest extends CustomerCommentsDao {

    private String currentSql;
    private List<Pair<String, String>> currentParameters;
    private List<AuditAttachmentDto> resultList;

    @BeforeMethod
    public void cleanUpResults(){
        resultList = new ArrayList<>();
    }

    @Test
    public void searchAllTest() throws RPException {
        resultList.add(new AuditAttachmentDto());
        resultList.add(new AuditAttachmentDto());
        List<CustomerCommentDto> result = searchAll(new CustomerCommentDto());
        assertSQLToParams(currentSql, currentParameters);
        assertEquals(result.size(), 2);
    }

    @Test
    public void insertTest() throws RPException {
        resultList.add(new AuditAttachmentDto());
        create(new CustomerCommentDto());
        assertSQLToParams(currentSql, currentParameters);
    }

    @Test
    public void removeTest() throws RPException {
        delete(new CustomerCommentDto());
        assertSQLToParams(currentSql, 0);
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
