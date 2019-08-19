package main;

import main.controllers.*;
import main.controllers.Administration.AdministrationController;
import main.controllers.Administration.AppSettingsController;
import main.controllers.Project.ProjectController;
import main.controllers.Project.ProjectUserController;
import main.exceptions.AqualityException;
import main.model.db.dao.project.UserDao;
import main.model.db.imports.Importer;
import main.model.db.imports.TestNameNodeType;
import main.model.dto.ProjectUserDto;
import main.model.dto.TestRunDto;
import main.model.dto.UserDto;
import main.model.email.TestRunEmails;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.stream.Collectors;

public class Session {

    private UserDto user;
    private String session;
    public  ControllerFactory controllerFactory;

    public Session(String sessionId) throws AqualityException {
        if(isSessionValid(sessionId)){
            setUserMembership();
        }
        controllerFactory = new ControllerFactory(user);
    }

    public Session(){
        user = new UserDto();
        user.setAdmin(1);
        user.setUnit_coordinator(1);
        user.setManager(1);
        controllerFactory = new ControllerFactory(user);
    }

    public List<ProjectUserDto> getProjectPermissions(){
        return user.getProjectUsers();
    }

    public List<ProjectUserDto> getProjectPermissions(Integer projectId){
        return user.getProjectUsers().stream().filter(x -> x.getProject_id().equals(projectId)).collect(Collectors.toList());
    }

    public Importer getImporter(List<String> filePaths, TestRunDto testRunTemplate, String pattern, String format, TestNameNodeType nodeType, boolean singleTestRun) throws AqualityException {
        try {
            return new Importer(filePaths, testRunTemplate, pattern, format, nodeType, singleTestRun, user);
        } catch (ParserConfigurationException | SAXException e) {
            throw new AqualityException("Some Internal SAX error: " + e.getMessage());
        }
    }

    public TestRunEmails getTestRunEmails(){
        return new TestRunEmails();
    }

    public AuditController getAuditController() {
        return new AuditController(user);
    }

    public AdministrationController getAdministrationController() {
        return new AdministrationController(user);
    }

    public ProjectController getProjectController () throws AqualityException {
        return new ProjectController(user);
    }

    public CustomerController getCustomerController () {
        return new CustomerController(user);
    }

    public AppSettingsController getSettingsController () {
        return new AppSettingsController(user);
    }

    public UserDto getCurrentUser() {
        return user;
    }

    public void setCurrentUser(UserDto user) throws AqualityException {
        this.user = user;
        setUserMembership();
    }

    private void setUserMembership() throws AqualityException {
        ProjectUserDto projectUserDto = new ProjectUserDto();
        projectUserDto.setUser_id(user.getId());
        user.setProjectUsers(new ProjectUserController(user).getProjectUserForPermissions(projectUserDto));
    }

    public boolean isSessionValid() throws AqualityException {
        return  isSessionValid(session);
    }

    private boolean isSessionValid(String sessionId) throws AqualityException {
        if(sessionId != null){
            UserDao userDao = new UserDao();
            user = userDao.IsAuthorized(sessionId);
            session = sessionId;
            return user != null;
        }
        user = null;
        return false;
    }
}
