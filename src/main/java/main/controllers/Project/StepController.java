package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.Step2TestDao;
import main.model.db.dao.project.StepDao;
import main.model.dto.Step2TestDto;
import main.model.dto.StepDto;
import main.model.dto.UserDto;

import java.util.List;

public class StepController extends BaseController<StepDto> {
    private StepDao stepDao;
    private Step2TestDao step2TestDao;

    public StepController(UserDto user) {
        super(user);
        stepDao = new StepDao();
        step2TestDao = new Step2TestDao();
    }

    @Override
    public List<StepDto> get(StepDto entity) throws AqualityException {
        return stepDao.searchAll(entity);
    }

    @Override
    public StepDto create(StepDto entity) throws AqualityException {
        return stepDao.create(entity);
    }

    @Override
    public boolean delete(StepDto entity) throws AqualityException {
        return stepDao.delete(entity);
    }

    public Step2TestDto assignToTest(Step2TestDto entity) throws AqualityException {
        return step2TestDao.create(entity);
    }

    public boolean removeFromTest(Step2TestDto entity) throws AqualityException {
        return step2TestDao.delete(entity);
    }

    public boolean updateOrder(List<Step2TestDto> entities) throws AqualityException {
        return step2TestDao.updateMultiply(entities);
    }
}
