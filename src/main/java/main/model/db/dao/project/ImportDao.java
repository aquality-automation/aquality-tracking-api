package main.model.db.dao.project;

import main.model.db.dao.DAO;
import main.model.dto.project.ImportDto;

public class ImportDao extends DAO<ImportDto> {
    public ImportDao() {
        super(ImportDto.class);
        select = "{call SELECT_IMPORTS(?,?,?)}";
        insert = "{call INSERT_IMPORT(?,?,?,?,?,?,?)}";
    }
}
