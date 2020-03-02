package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.MilestoneDao;
import main.model.db.dao.project.Suite2MilestoneDao;
import main.model.dto.MilestoneDto;
import main.model.dto.Suite2MilestoneDto;
import main.model.dto.TestSuiteDto;
import main.model.dto.UserDto;

import java.util.List;
import java.util.Objects;

public class MilestoneController extends BaseController<MilestoneDto> {
    private MilestoneDao milestoneDao;
    private Suite2MilestoneDao milestoneSuitesDao;

    public MilestoneController(UserDto user) {
        super(user);
        milestoneDao = new MilestoneDao();
        milestoneSuitesDao = new Suite2MilestoneDao();
    }

    @Override
    public MilestoneDto create(MilestoneDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            MilestoneDto milestone = milestoneDao.create(template);
            template.setId(milestone.getId());
            return updateSuites(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Milestones", baseUser);
        }
    }

    @Override
    public List<MilestoneDto> get(MilestoneDto template) throws AqualityException {
        if (baseUser.isFromGlobalManagement() || baseUser.getProjectUser(template.getProject_id()).isViewer()) {
            List<MilestoneDto> milestones = milestoneDao.searchAll(template);
            return fillMilestones(milestones);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to view Milestones", baseUser);
        }
    }

    @Override
    public boolean delete(MilestoneDto template) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(template.getProject_id()).isEditor()) {
            return milestoneDao.delete(template);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Milestones", baseUser);
        }
    }

    private List<MilestoneDto> fillMilestones(List<MilestoneDto> milestones) throws AqualityException {
        for (MilestoneDto milestone : milestones) {
            milestone.setSuites(getSuites(milestone.getId()));
        }

        return milestones;
    }

    private List<TestSuiteDto> getSuites(Integer milestoneId) throws AqualityException {
        Suite2MilestoneDto suite2Milestone = new Suite2MilestoneDto();
        suite2Milestone.setMilestone_id(milestoneId);
        return milestoneSuitesDao.getSuites(suite2Milestone);
    }

    private MilestoneDto updateSuites(MilestoneDto template) throws AqualityException {
        Suite2MilestoneDto suite2Milestone = new Suite2MilestoneDto();
        suite2Milestone.setMilestone_id(template.getId());
        List<TestSuiteDto> oldSuites = milestoneSuitesDao.getSuites(suite2Milestone);

        addNewSuites(oldSuites, template);
        removeSuites(oldSuites, template.getId());

        List<MilestoneDto> milestones = get(template);
        if(!milestones.isEmpty()) {
            return milestones.get(0);
        }

        throw new AqualityException("Cannot get Milestone after Suites Update!");
    }

    private void addNewSuites(List<TestSuiteDto> oldSuites, MilestoneDto milestone) throws AqualityException {
        if (milestone.getSuites() != null && milestone.getSuites().size() > 0) {
            for (TestSuiteDto newSuite : milestone.getSuites()) {
                TestSuiteDto alreadyExists = oldSuites.stream().filter(x -> Objects.equals(x.getId(), newSuite.getId())).findAny().orElse(null);
                if (alreadyExists != null) {
                    oldSuites.removeIf(x -> Objects.equals(x.getId(), alreadyExists.getId()));
                } else {
                    Suite2MilestoneDto newSuite2MilestoneDto = new Suite2MilestoneDto();
                    newSuite2MilestoneDto.setSuite_id(newSuite.getId());
                    newSuite2MilestoneDto.setMilestone_id(milestone.getId());
                    milestoneSuitesDao.addSuite(newSuite2MilestoneDto);
                }
            }
        }
    }

    private void removeSuites(List<TestSuiteDto> oldSuites, Integer milestone_id) throws AqualityException {
        if (!oldSuites.isEmpty()) {
            for (TestSuiteDto oldSuite : oldSuites) {
                Suite2MilestoneDto toRemove = new Suite2MilestoneDto();
                toRemove.setSuite_id(oldSuite.getId());
                toRemove.setMilestone_id(milestone_id);
                milestoneSuitesDao.delete(toRemove);
            }
        }
    }
}
