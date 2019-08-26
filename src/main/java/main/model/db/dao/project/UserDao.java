package main.model.db.dao.project;

import main.controllers.Project.ImportTokenController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.DAO;
import main.model.dto.ProjectDto;
import main.model.dto.ProjectUserDto;
import main.model.dto.UserDto;
import main.utils.DateUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserDao extends DAO<UserDto> {
    public UserDao() {
        super(UserDto.class);
        select = "{call SELECT_USERS(?,?,?,?,?,?,?)}";
        insert = "{call INSERT_USER(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_USER(?)}";
    }

    /**
     * Check id session or token is valid
     * @param sessionId Session ID = {username}:{uuid}:{creationDate} or project:{projectId}:{token}
     * @return User for session
     * @throws AqualityException error about authorization status
     */
    public UserDto IsAuthorized(String sessionId) throws AqualityException {
        if(sessionId.startsWith("project")){
            return IsAuthorizedToken(sessionId);
        }

        return IsAuthorizedUser(sessionId);
    }

    /**
     * Check If User Authorized
     * @param sessionId Session ID = {username}:{uuid}:{creationDate}
     * @return Authorized User
     * @throws AqualityException error about authorization status
     */
    private UserDto IsAuthorizedUser(String sessionId) throws AqualityException {
        Base64 base64 = new Base64();
        String[] strings = StringUtils.newStringUtf8(base64.decode(sessionId)).split(":");
        DateUtils dates = new DateUtils();
        UserDto user = new UserDto();
        user.setUser_name(strings[0]);
        List<UserDto> users = searchAll(user);
        if(users.size() > 0){
            user = users.get(0);
            if(!user.getSession_code().equals(sessionId)){
                throw new AqualityPermissionsException("Credentials you've provided are not valid. Reenter please.", user);
            }
            if(new Date().after(dates.fromyyyyMMdd(strings[2]))){
                throw new AqualityPermissionsException("Session Expired.", user);
            }
        }
        else{
            throw new AqualityPermissionsException("Credentials you've provided are not valid. Reenter please.", user);
        }
        return user;
    }

    /**
     * Check If Token Valid
     * @param sessionId Session ID = project:{projectId}:{token}
     * @return Project Engineer
     * @throws AqualityException error about authorization status
     */
    private UserDto IsAuthorizedToken(String sessionId) throws AqualityException {
        Base64 base64 = new Base64();
        String[] strings = StringUtils.newStringUtf8(base64.decode(sessionId)).split(":");
        boolean isTokenValid = new ImportTokenController(new UserDto()).isTokenValid(strings[2], Integer.parseInt(strings[1]));

        if(isTokenValid){
            UserDto user = new UserDto();
            ProjectUserDto projectUser = new ProjectUserDto();
            List<ProjectUserDto> projectUsersList = new ArrayList<>();
            projectUsersList.add(projectUser);
            user.setProjectUsers(projectUsersList);
            return user;
        }
        else{
            throw new AqualityPermissionsException("The import token is not valid", null);
        }
    }
}
