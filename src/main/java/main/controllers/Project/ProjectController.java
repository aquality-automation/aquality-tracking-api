package main.controllers.Project;

import main.controllers.BaseController;
import main.controllers.CustomerController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.*;
import main.model.dto.*;

import java.util.*;

public class ProjectController extends BaseController<ProjectDto> {
    private ProjectDao projectDao;
    private CustomerController customerController;

    public ProjectController(UserDto user) {
        super(user);
        projectDao = new ProjectDao();
        customerController = new CustomerController(user);
    }

    @Override
    public ProjectDto create(ProjectDto template) throws AqualityException {
        if(baseUser.isAdmin() || allowUpdateProject(template)){
            ProjectDto project = projectDao.create(template);
            updateProjectPermissions(project);
            return project;
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Projects", baseUser);
        }
    }

    @Override
    public List<ProjectDto> get(ProjectDto template) throws AqualityException {
        if (baseUser.getApiSessionProjectId() != null) {
            template.setId(baseUser.getApiSessionProjectId());
        } else {
            template.setUser_id(baseUser.getId());
        }
        List<ProjectDto> projects = projectDao.searchAll(template);
        return fillCustomers(projects);
    }

    @Override
    public boolean delete(ProjectDto template) throws AqualityException {
        if(baseUser.isAdmin()){
            return projectDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Projects", baseUser);
        }
    }

    public boolean isStepsEnabled(Integer projectId) throws AqualityException {
        ProjectDto project = new ProjectDto();
        project.setId(projectId);
        List<ProjectDto> projects = get(project);

        if(projects.size() < 1) {
            throw new AqualityException("Project with id %s does not exists!", projectId);
        }

        return projects.get(0).getSteps() == 1;
    }

    private void updateProjectPermissions(ProjectDto entity) throws AqualityException {
        if(entity.getCustomer() != null && entity.getCustomer().getAccounting() == 1 && entity.getId() != 0){
            updatePermissions(entity.getCustomer().getId(), entity.getId());
        }
    }

    private boolean allowUpdateProject(ProjectDto template) {
        if(template.getId() != null) {
            ProjectUserDto projectUser = baseUser.getProjectUser(template.getId());
            return baseUser.isManager() || projectUser.isManager() || projectUser.isAdmin();
        }
        return false;
    }

    //TODO create
    private void updatePermissions(Integer customer_id, Integer project_id) throws AqualityException {
    }

    //TODO Refactoring
    private List<ProjectDto> fillCustomers(List<ProjectDto> projects) throws AqualityException {
        List<ProjectDto> filledProjects = new ArrayList<>();
        List<CustomerDto> customerDtoList = customerController.get(new CustomerDto(), true);
        for (ProjectDto filledProject : projects) {
            if (filledProject.getCustomer_id() != null) {
                int customerId = filledProject.getCustomer_id();
                filledProject.setCustomer(customerDtoList.stream().filter(x -> x.getId() == customerId).findFirst().orElse(null));
            } else {
                filledProject.setCustomer(null);
            }
            filledProjects.add(filledProject);
        }
        return filledProjects;
    }




}
