package main.controllers;

import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.customer.CustomerDao;
import main.model.db.dao.project.ProjectDao;
import main.model.db.dao.project.UserDao;
import main.model.dto.*;

import java.util.ArrayList;
import java.util.List;

public class CustomerController extends BaseController<CustomerDto> {
    private CustomerDao customerDao;
    private UserDao userDao;

    public CustomerController(UserDto user) {
        super(user);
        customerDao = new CustomerDao();
        userDao = new UserDao();
    }

    @Override
    public List<CustomerDto> get(CustomerDto template) throws AqualityException {
        return get(template, false);
    }

    public List<CustomerDto> get(CustomerDto template, boolean withChildren) throws AqualityException {
        List<CustomerDto> customers = customerDao.searchAll(template);
        if (withChildren) {
            return fillCustomers(customers);
        } else {
            return customers;
        }
    }

    @Override
    public CustomerDto create(CustomerDto template) throws AqualityException {
        if(baseUser.isCoordinator()){
            return fillCustomer(customerDao.create(template));
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Customers", baseUser);
        }
    }

    @Override
    public boolean delete(CustomerDto template) throws AqualityException {
        if(baseUser.isCoordinator()){
            return customerDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete Customers", baseUser);
        }
    }

    private List<CustomerDto> fillCustomers(List<CustomerDto> customers) throws AqualityException {
        List<UserDto> users = userDao.getAll();
        for (CustomerDto customer: customers){
            customer.setCoordinator(users.stream().filter(x -> x.getId().equals(customer.getCoordinator_id())).findFirst().orElse(null));
            customer.getCoordinator().toPublic();
            if(baseUser.isCoordinator()){
                customer.setProjects(getProjects(customer));
            }
        }

        return customers;
    }

    private List<ProjectDto> getProjects(CustomerDto customer) throws AqualityException {
        ProjectDao projectDao = new ProjectDao();
        ProjectDto projectTemplate = new ProjectDto();
        projectTemplate.setCustomer_id(customer.getId());
        return projectDao.searchAll(projectTemplate);
    }

    private CustomerDto fillCustomer(CustomerDto customer) throws AqualityException {
        List<CustomerDto> customers = new ArrayList<>();
        customers.add(customer);
        return fillCustomers(customers).get(0);
    }
}
