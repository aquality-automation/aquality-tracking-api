package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.project.FinalResultDao;
import main.model.dto.FinalResultDto;
import main.model.dto.UserDto;

import java.util.List;

public class FinalResultController extends BaseController<FinalResultDto> {
    private FinalResultDao finalResultDao;

    public FinalResultController(UserDto user) {
        super(user);
        finalResultDao = new FinalResultDao();
    }

    @Override
    public FinalResultDto create(FinalResultDto template) throws RPException {
        if(baseUser.isAdmin()){
            return finalResultDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Final Result", baseUser);
        }
    }

    @Override
    public List<FinalResultDto> get(FinalResultDto template) throws  RPException {
        return finalResultDao.searchAll(template);
    }

    @Override
    public boolean delete(FinalResultDto template) throws  RPException {
        if(baseUser.isAdmin()){
            return finalResultDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Final Result", baseUser);
        }
    }
}
