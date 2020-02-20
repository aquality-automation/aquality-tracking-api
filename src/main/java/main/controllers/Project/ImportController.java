package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.ImportDao;
import main.model.dto.ImportDto;
import main.model.dto.UserDto;
import org.apache.poi.util.NotImplemented;

import java.util.List;

public class ImportController extends BaseController<ImportDto> {
    private ImportDao importDao;

    public ImportController(UserDto user) {
        super(user);
        importDao = new ImportDao();
    }

    @Override
    public List<ImportDto> get(ImportDto template) throws AqualityException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return importDao.searchAll(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to view Imports", baseUser);
        }
    }

    @Override @NotImplemented
    public ImportDto create(ImportDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    @Override @NotImplemented
    public boolean delete(ImportDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }
}
