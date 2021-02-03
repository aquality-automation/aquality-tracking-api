package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.references.ReferenceDto;
import main.model.dto.integrations.references.ReferenceType;

public class ReferenceDao<T extends ReferenceDto> extends DAO<T> {

    public ReferenceDao(ReferenceType<T> referenceType) {
        super(referenceType.getDtoClass());
        select ="{call SELECT_INT_REFERENCE(?,?,?,?,?,?)}";
        insert = "{call INSERT_INT_REFERENCE(?,?,?,?,?,?)}";
        remove = "{call REMOVE_INT_REFERENCE(?,?)}";
    }
}
