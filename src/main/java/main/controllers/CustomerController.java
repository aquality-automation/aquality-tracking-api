package main.controllers;

import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.customer.CustomerAttachmentsDao;
import main.model.db.dao.customer.CustomerCommentsDao;
import main.model.db.dao.customer.CustomerDao;
import main.model.db.dao.customer.CustomerMembersDao;
import main.model.db.dao.project.ProjectDao;
import main.model.db.dao.project.ProjectUserDao;
import main.model.db.dao.project.UserDao;
import main.model.dto.*;
import main.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerController extends BaseController<CustomerDto> {
    private CustomerDao customerDao;
    private CustomerAttachmentsDao customerAttachmentsDao;
    private CustomerCommentsDao customerCommentsDao;
    private CustomerMembersDao customerMembersDao;
    private UserDao userDao;
    private ProjectUserDao projectUserDao;

    public CustomerController(UserDto user) {
        super(user);
        customerDao = new CustomerDao();
        customerAttachmentsDao = new CustomerAttachmentsDao();
        customerCommentsDao = new CustomerCommentsDao();
        customerMembersDao = new CustomerMembersDao();
        projectUserDao = new ProjectUserDao();
        userDao = new UserDao();
    }

    @Override
    public List<CustomerDto> get(CustomerDto template) throws RPException {
        return get(template, false);
    }

    public List<CustomerDto> get(CustomerDto template, boolean withChildren) throws RPException {
        List<CustomerDto> customers = customerDao.searchAll(template);
        if (withChildren) {
            return fillCustomers(customers);
        } else {
            return customers;
        }
    }

    public List<CustomerMemberDto> get(CustomerMemberDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return customerMembersDao.searchAll(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Customer Members", baseUser);
        }
    }

    public List<CustomerCommentDto> get(CustomerCommentDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return completeComments(customerCommentsDao.searchAll(template));
        }else{
            throw new RPPermissionsException("Account is not allowed to view Customer Comments", baseUser);
        }
    }

    public List<CustomerAttachmentDto> get(CustomerAttachmentDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return customerAttachmentsDao.searchAll(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Customer Comments", baseUser);
        }
    }

    @Override
    public CustomerDto create(CustomerDto template) throws RPException {
        if(baseUser.isCoordinator()){
            if(template.getId() != null && template.getAccounting() == 0){
                template.setAccount_manager(new UserDto());
                template.getAccount_manager().setId(0);
                // TODO: Remove Customer Members
            }

            CustomerDto customer = fillCustomer(customerDao.create(template));

            if(template.getAccounting() != null && template.getAccounting() == 1 && template.getAccount_manager() != null){
                addPermissions(template.getAccount_manager().getId(), customer);
            }
            return customer;
        }else{
            throw new RPPermissionsException("Account is not allowed to create Customers", baseUser);
        }
    }
    public CustomerAttachmentDto create(CustomerAttachmentDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return customerAttachmentsDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Customer Attachment", baseUser);
        }
    }
    public CustomerCommentDto create(CustomerCommentDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return customerCommentsDao.create(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to create Customer Comment", baseUser);
        }
    }

    // TODO: update members
    public  void updateCustomerMember(List<CustomerMemberDto> members){
        throw new UnsupportedOperationException("Not implemented Update Members function");
    }

    @Override
    public boolean delete(CustomerDto template) throws RPException {
        if(baseUser.isCoordinator()){
            return customerDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to delete Customers", baseUser);
        }
    }
    public boolean delete(CustomerAttachmentDto template) throws RPException {
        if(baseUser.isCoordinator()){
            FileUtils fileUtils = new FileUtils();
            template = get(template).get(0);
            List<String> pathes = new ArrayList<>();
            pathes.add(template.getPath());
            fileUtils.removeFiles(pathes);
            return customerAttachmentsDao.delete(template);
        }else{
            throw new RPPermissionsException("Account is not allowed to view Customers", baseUser);
        }
    }

    private List<CustomerDto> fillCustomers(List<CustomerDto> customers) throws RPException {
        List<UserDto> users = userDao.getAll();

        for (CustomerDto customer: customers){
            customer.setCoordinator(users.stream().filter(x -> x.getId().equals(customer.getCoordinator_id())).findFirst().orElse(null));
            customer.getCoordinator().toPublic();

            if(customer.getAccounting() == 1){
                Integer accountManagerId = customer.getAccount_manager_id();
                customer.setAccount_manager(users.stream().filter(x->x.getId().equals(accountManagerId)).findFirst().orElse(null));
                customer.getAccount_manager().toPublic();
            }

            if(baseUser.isCoordinator()){
                customer.setAccount_team(getMembers(customer));
                customer.setComments(getComments(customer));
                customer.setAttachments(getAttachments(customer));
                customer.setProjects(getProjects(customer));
            }
        }

        return customers;
    }

    private List<ProjectDto> getProjects(CustomerDto customer) throws RPException {
        ProjectDao projectDao = new ProjectDao();
        ProjectDto projectTemplate = new ProjectDto();
        projectTemplate.setCustomer_id(customer.getId());
        return projectDao.searchAll(projectTemplate);
    }

    private List<CustomerAttachmentDto> getAttachments(CustomerDto customer) throws RPException {
        CustomerAttachmentDto customerAttachmentDtoTemplate = new CustomerAttachmentDto();
        customerAttachmentDtoTemplate.setCustomer_id(customer.getId());
        return get(customerAttachmentDtoTemplate);
    }

    private List<CustomerCommentDto> getComments(CustomerDto customer) throws RPException {
        CustomerCommentDto customerCommentDtoTemplate = new CustomerCommentDto();
        customerCommentDtoTemplate.setCustomer_id(customer.getId());
        return get(customerCommentDtoTemplate);
    }

    private List<CustomerMemberDto> getMembers(CustomerDto customer) throws RPException {
        CustomerMemberDto customerMemberDto = new CustomerMemberDto();
        customerMemberDto.setCustomer_id(customer.getId());
        return get(customerMemberDto);
    }

    private CustomerDto fillCustomer(CustomerDto customer) throws RPException {
        List<CustomerDto> customers = new ArrayList<>();
        customers.add(customer);
        return fillCustomers(customers).get(0);
    }

    private void addPermissions(Integer user_id, CustomerDto customerDto) throws RPException {
        List<ProjectDto> projects = customerDto.getProjects();

        for (ProjectDto project : projects) {
            ProjectUserDto projectUserDto = new ProjectUserDto();
            UserDto userDto = new UserDto();
            userDto.setId(user_id);
            projectUserDto.setProject_id(project.getId());
            projectUserDto.setUser(userDto);
            projectUserDto.setViewer(1);
            projectUserDao.create(projectUserDto);
        }
    }

    private List<CustomerCommentDto> completeComments(List<CustomerCommentDto> comments) throws RPException {
        UserDto userTemplate = new UserDto();

        for (CustomerCommentDto comment: comments){

            if(comment.getUser_id() != null){
                userTemplate.setId(comment.getUser_id());
            }else{
                userTemplate.setId(1);
            }
            try{
                comment.setAuthor(userDao.getEntityById(userTemplate));
            } catch (Exception e){
                throw new RPException("Cannot find author for Customer Comment");
            }
        }
        return comments;
    }
}
