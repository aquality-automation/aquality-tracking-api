package main.model.db.dao.settings;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.settings.EmailSettingsDto;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class EmailSettingsDao extends DAO<EmailSettingsDto> {

    public EmailSettingsDao() {
        super(EmailSettingsDto.class);
        insert = "{call INSERT_EMAIL_SETTINGS(?,?,?,?,?,?,?,?,?,?)}";
        select = "{call SELECT_EMAIL_SETTINGS(?)}";
    }

    @Override
    public EmailSettingsDto create(EmailSettingsDto entity) throws AqualityException {
        entity.setPassword(hideAdminSecret(entity.getPassword()));
        List<Pair<String, String>> parameters = entity.getParameters();

        return dtoMapper.mapObjects(CallStoredProcedure(insert, parameters).toString()).get(0);
    }

    public Integer isEmailsEnabled() throws AqualityException {
        EmailSettingsDto settings = getAll().get(0);
        return settings.getEnabled();
    }

    private String hideAdminSecret(String secret) throws AqualityException {
        Base64 base64 = new Base64();
        try {
            secret = base64.encodeToString(("JmbGFzYmRmamtiYXNsZA"+secret+"qYXNkaGxma2poYXNka2xqZmJka2").getBytes("utf-8"));
            return base64.encodeToString(("YXNkamhmbGtqYXNkaGx"+secret+"a2poYXNka2xqZmJka2phc2JmbGFzYmRmamtiYXNsZA").getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new AqualityException(String.format("Encoding Error: %s", e.getMessage()));
        }
    }

    public String getAdminSecret(String hiddenSecret){
        Base64 base64 = new Base64();
        String level1 = StringUtils.newStringUtf8(base64.decode(hiddenSecret));
        level1 = level1.replace("YXNkamhmbGtqYXNkaGx", "");
        level1 = level1.replace("a2poYXNka2xqZmJka2phc2JmbGFzYmRmamtiYXNsZA", "");
        String level2 = StringUtils.newStringUtf8(base64.decode(level1));
        level2 = level2.replace("JmbGFzYmRmamtiYXNsZA", "");
        level2 = level2.replace("qYXNkaGxma2poYXNka2xqZmJka2", "");
        return level2;
    }
}
