package main.model.db.dao.audit;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.AuditorDto;
import main.model.dto.UserDto;

import java.util.List;

public class AuditorsDao extends DAO<AuditorDto> {
    public AuditorsDao() {
        super(AuditorDto.class);
        select = "{call SELECT_AUDITOR(?,?,?)}";
        insert = "{call INSERT_AUDITOR(?,?)}";
        remove = "{call REMOVE_AUDITOR(?)}";
    }

    @Override
    public List<AuditorDto> searchAll(AuditorDto entity) throws AqualityException {
        List<Pair<String, String>> parameters = entity.getSearchParameters();
        checkSelectProcedure();
        List<AuditorDto> auditors = dtoMapper.mapObjects(CallStoredProcedure(select, parameters).toString());
        auditors.forEach(UserDto::toPublic);
        return auditors;
    }
}
