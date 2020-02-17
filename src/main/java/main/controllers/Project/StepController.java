package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.Step2TestDao;
import main.model.db.dao.project.StepDao;
import main.model.dto.Step2TestDto;
import main.model.dto.StepDto;
import main.model.dto.UserDto;

import java.util.List;
import java.util.Objects;

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

    public List<StepDto> updateOrder(List<Step2TestDto> entities) throws AqualityException {
        if (entities.size() < 1) {
            throw new AqualityException("The test should have at least one step!");
        }
        Integer projectId = entities.get(0).getProject_id();
        Step2TestDto step2TestFilter = new Step2TestDto();
        step2TestFilter.setProject_id(projectId);
        step2TestFilter.setTest_id(entities.get(0).getTest_id());
        List<StepDto> oldSteps = getTestSteps(step2TestFilter);

        for (Step2TestDto newStepLink : entities) {
            StepDto alreadyExists = oldSteps.stream().filter(x -> Objects.equals(x.getLink_id(), newStepLink.getId())).findFirst().orElse(null);
            if (alreadyExists != null) {
                oldSteps.removeIf(x -> Objects.equals(x.getId(), alreadyExists.getId()));
            }
            step2TestDao.create(newStepLink);
        }

        if (oldSteps.size() > 0) {
            for (StepDto oldStep : oldSteps) {
                Step2TestDto stepToRemove = new Step2TestDto();
                stepToRemove.setId(oldStep.getLink_id());
                stepToRemove.setProject_id(projectId);
                step2TestDao.delete(stepToRemove);
            }
        }

        return getTestSteps(step2TestFilter);
    }

    public List<StepDto> getTestSteps(Step2TestDto stepToTest) throws AqualityException {
        return step2TestDao.getTestSteps(stepToTest);
    }
}
