package main.model.db.dao.project;


import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.RPException;
import main.model.db.dao.DAO;
import main.model.dto.ImportTokenDto;
import main.utils.RandomStringGenerator;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.List;

public class ImportTokenDao extends DAO<ImportTokenDto> {

    public ImportTokenDao(){
        super(ImportTokenDto.class);
        select = "{call SELECT_IMPORT_TOKEN(?)}";
        insert = "{call INSERT_IMPORT_TOKEN(?,?)}";
    }

    @Override
    public ImportTokenDto create(ImportTokenDto entity) throws RPException {
        String token = generateToken();
        entity.setImport_token(DigestUtils.md5Hex(token + "advbc1671-nlksdui-ff"));
        List<Pair<String, String>> parameters = entity.getParameters();

        CallStoredProcedure(insert, parameters);
        entity.setImport_token(token);
        return entity;
    }

    private String generateToken(){
        Date date = new Date();
        return RandomStringGenerator.generateString() + date.getTime();
    }
}
