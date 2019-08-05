package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.ImportDao;
import main.model.dto.ImportDto;
import main.model.dto.UserDto;
import org.apache.poi.util.NotImplemented;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ImportController extends BaseController<ImportDto> {
    private ImportDao importDao;

    public ImportController(UserDto user) {
        super(user);
        importDao = new ImportDao();
    }

    @Override
    public List<ImportDto> get(ImportDto template) throws RPException {
        if(baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()){
            return importDao.searchAll(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Imports", baseUser);
        }
    }

    @Override @NotImplemented
    public ImportDto create(ImportDto entity) throws RPException {
        throw new NotImplementedException();
    }

    @Override @NotImplemented
    public boolean delete(ImportDto entity) throws RPException {
        throw new NotImplementedException();
    }
}
