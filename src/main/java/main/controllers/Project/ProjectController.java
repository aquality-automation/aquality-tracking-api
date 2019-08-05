package main.controllers.Project;

import main.controllers.BaseController;
import main.controllers.CustomerController;
import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
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
    public ProjectDto create(ProjectDto template) throws RPException {
        if(baseUser.isAdmin()){
            ProjectDto project = projectDao.create(template);
            updateProjectPermissions(project);
            return project;
        }else{
            throw new RPPermissionsException("Account is not allowed to create Projects", baseUser);
        }
    }

    @Override
    public List<ProjectDto> get(ProjectDto template) throws  RPException {
        template.setUser_id(baseUser.getId());
        List<ProjectDto> projects = projectDao.searchAll(template);
        return fillCustomers(projects);
    }

    @Override
    public boolean delete(ProjectDto template) throws RPException {
        if(baseUser.isAdmin()){
            return projectDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Projects", baseUser);
        }
    }

    private void updateProjectPermissions(ProjectDto entity) throws RPException {
        if(entity.getCustomer() != null && entity.getCustomer().getAccounting() == 1 && entity.getId() != 0){
            updatePermissions(entity.getCustomer().getId(), entity.getId());
        }
    }

    //TODO create
    private void updatePermissions(Integer customer_id, Integer project_id) throws RPException {
    }

    //TODO Refactoring
    private List<ProjectDto> fillCustomers(List<ProjectDto> projects) throws RPException {
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
