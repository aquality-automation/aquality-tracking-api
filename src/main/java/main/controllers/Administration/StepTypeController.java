package main.controllers.Administration;

import main.controllers.BaseController;
import main.controllers.IController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.StepTypeDao;
import main.model.dto.StepTypeDto;
import main.model.dto.UserDto;

import java.util.List;

public class StepTypeController extends BaseController<StepTypeDto> implements IController<StepTypeDto> {
    private StepTypeDao stepTypeDao;

    public StepTypeController(UserDto user) {
        super(user);
        stepTypeDao = new StepTypeDao();
    }

    @Override
    public List<StepTypeDto> get(StepTypeDto entity) throws AqualityException {
        return stepTypeDao.searchAll(entity);
    }

    @Override
    public StepTypeDto create(StepTypeDto entity) throws AqualityException {
        return stepTypeDao.create(entity);
    }

    @Override
    public boolean delete(StepTypeDto entity) throws AqualityException {
        return stepTypeDao.delete(entity);
    }
}
