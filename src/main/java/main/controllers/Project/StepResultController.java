package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.StepResultDao;
import main.model.dto.project.StepResultDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class StepResultController extends BaseController<StepResultDto> {
    private StepResultDao stepResultDao;

    public StepResultController(UserDto user) {
        super(user);
        stepResultDao = new StepResultDao();
    }

    @Override
    public List<StepResultDto> get(StepResultDto entity) throws AqualityException {
        return stepResultDao.searchAll(entity);
    }

    @Override
    public StepResultDto create(StepResultDto entity) throws AqualityException {
        return stepResultDao.create(entity);
    }

    @Override
    public boolean delete(StepResultDto entity) throws AqualityException {
        return stepResultDao.delete(entity);
    }
}
