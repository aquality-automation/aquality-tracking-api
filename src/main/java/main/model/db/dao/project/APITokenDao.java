package main.model.db.dao.project;


import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.project.APITokenDto;
import main.utils.RandomStringGenerator;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.List;

public class APITokenDao extends DAO<APITokenDto> {

    public APITokenDao(){
        super(APITokenDto.class);
        select = "{call SELECT_API_TOKEN(?)}";
        insert = "{call INSERT_API_TOKEN(?,?)}";
    }

    @Override
    public APITokenDto create(APITokenDto entity) throws AqualityException {
        String token = generateToken();
        entity.setApi_token(DigestUtils.md5Hex(token + "advbc1671-nlksdui-ff"));
        List<Pair<String, String>> parameters = entity.getParameters();
        checkInsertProcedure();
        CallStoredProcedure(insert, parameters);
        entity.setApi_token(token);
        return entity;
    }

    private String generateToken(){
        Date date = new Date();
        return RandomStringGenerator.generateString() + date.getTime();
    }
}
