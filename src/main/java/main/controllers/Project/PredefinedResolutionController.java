package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.PredefinedResolutionDao;
import main.model.dto.*;

import java.util.List;

public class PredefinedResolutionController extends BaseController<PredefinedResolutionDto> {
    private PredefinedResolutionDao predefinedResolutionDao;
    private ProjectUserController projectUserController;
    private ResultResolutionController resultResolutionController;

    public PredefinedResolutionController(UserDto user) {
        super(user);
        predefinedResolutionDao = new PredefinedResolutionDao();
        projectUserController = new ProjectUserController(user);
        resultResolutionController = new ResultResolutionController(user);
    }

    @Override
    public List<PredefinedResolutionDto> get(PredefinedResolutionDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return fillPredefinedResolutions(predefinedResolutionDao.searchAll(entity));
        } else {
            throw new AqualityPermissionsException("Account is not allowed to get Predefined Resolutions", baseUser);
        }
    }

    @Override
    public PredefinedResolutionDto create(PredefinedResolutionDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return predefinedResolutionDao.create(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to create Predefined Resolutions", baseUser);
        }
    }

    @Override
    public boolean delete(PredefinedResolutionDto entity) throws AqualityException {
        if (baseUser.isManager() || baseUser.getProjectUser(entity.getProject_id()).isEditor()) {
            return predefinedResolutionDao.delete(entity);
        } else {
            throw new AqualityPermissionsException("Account is not allowed to delete Predefined Resolutions", baseUser);
        }
    }

    private List<PredefinedResolutionDto> fillPredefinedResolutions(List<PredefinedResolutionDto> p_resolutions) throws AqualityException {
        if (p_resolutions.size() > 0) {
            List<ResultResolutionDto> resolutions = resultResolutionController.get(new ResultResolutionDto());

            ProjectUserDto projectUserDto = new ProjectUserDto();
            projectUserDto.setProject_id(p_resolutions.get(0).getProject_id());
            List<ProjectUserDto> projectUsers = projectUserController.get(projectUserDto);

            for (PredefinedResolutionDto p_resolution : p_resolutions) {
                fillPredefinedResolution(p_resolution, resolutions, projectUsers);
            }
        }

        return p_resolutions;
    }

    private void fillPredefinedResolution(PredefinedResolutionDto p_resolution, List<ResultResolutionDto> resolutions, List<ProjectUserDto> users) {
        resolutions.stream().filter(x -> x.getId().equals(p_resolution.getResolution_id())).findFirst().ifPresent(p_resolution::setResolution);
        if (p_resolution.getAssignee() != null) {
            users.stream().filter(user -> user.getUser_id().equals(p_resolution.getAssignee())).findFirst()
                    .ifPresent(projectUser -> p_resolution.setAssigned_user(projectUser.getUser()));
        }
    }
}
