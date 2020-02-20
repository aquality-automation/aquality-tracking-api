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
            return project;
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Projects", baseUser);
        }
    }

    @Override
    public List<ProjectDto> get(ProjectDto template) throws AqualityException {
        return get(template, true);
    }

    public List<ProjectDto> get(ProjectDto template, boolean withChildren) throws AqualityException {
        if (baseUser.getApiSessionProjectId() != null) {
            template.setId(baseUser.getApiSessionProjectId());
        } else {
            template.setUser_id(baseUser.getId());
        }
        List<ProjectDto> projects = projectDao.searchAll(template);
        return fillCustomers(projects, withChildren);
    }

    @Override
    public boolean delete(ProjectDto template) throws AqualityException {
        if(baseUser.isAdmin()){
            return projectDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Projects", baseUser);
        }
    }

    boolean isStepsEnabled(Integer projectId) throws AqualityException {
        ProjectDto project = new ProjectDto();
        project.setId(projectId);
        List<ProjectDto> projects = get(project, false);

        if(projects.size() < 1) {
            throw new AqualityException("Project with id %s does not exists!", projectId);
        }

        return projects.get(0).getSteps() == 1;
    }

    private boolean allowUpdateProject(ProjectDto template) {
        if(template.getId() != null) {
            ProjectUserDto projectUser = baseUser.getProjectUser(template.getId());
            return baseUser.isManager() || projectUser.isManager() || projectUser.isAdmin();
        }
        return false;
    }

    private List<ProjectDto> fillCustomers(List<ProjectDto> projects, boolean withChildren) throws AqualityException {
        List<ProjectDto> filledProjects = new ArrayList<>();
        CustomerDto customerTemplate = new CustomerDto();
        if(projects.size() == 1) {
            customerTemplate.setId(projects.get(0).getCustomer_id());
        }

        List<CustomerDto> customerDtoList = customerController.get(new CustomerDto(), withChildren);

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
