package main.model.db.dao.project;

import main.controllers.Project.APITokenController;
import main.controllers.Project.ProjectUserController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.DAO;
import main.model.dto.project.ProjectUserDto;
import main.model.dto.settings.UserDto;
import main.utils.DateUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class UserDao extends DAO<UserDto> {
    public UserDao() {
        super(UserDto.class);
        select = "{call SELECT_USERS(?,?,?,?,?,?)}";
        insert = "{call INSERT_USER(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_USER(?)}";
    }

    /**
     * Check id session or token is valid
     * @param sessionHash Base64 String with Session ID = {username}:{uuid}:{creationDate} or project:{projectId}:{token}
     * @return User for session
     * @throws AqualityException error about authorization status
     */
    public UserDto GetAuthorizedUser(String sessionHash) throws AqualityException {
        Base64 base64 = new Base64();
        String sessionId = StringUtils.newStringUtf8(base64.decode(sessionHash));
        boolean isApiToken = sessionId.startsWith("project");
        if(isApiToken){
            return IsAuthorizedToken(sessionId);
        }
        return IsAuthorizedUser(sessionId, sessionHash);
    }

    /**
     * Check If User Authorized
     * @param sessionId Session ID = {username}:{uuid}:{creationDate}
     * @param sessionHash Hash of Session ID = {username}:{uuid}:{creationDate}
     * @return Authorized User
     * @throws AqualityException error about authorization status
     */
    private UserDto IsAuthorizedUser(String sessionId, String sessionHash) throws AqualityException {
        String[] strings = sessionId.split(":");
        DateUtils dates = new DateUtils();
        UserDto user = new UserDto();
        user.setUser_name(strings[0]);
        List<UserDto> users = searchAll(user);

        if(users.size() > 0){
            user = users.get(0);
            if (user.getSession_code().equals(sessionHash)) {
                if (new Date().before(dates.fromyyyyMMdd(strings[2]))) {
                    ProjectUserDto projectUserDto = new ProjectUserDto();
                    projectUserDto.setUser_id(user.getId());
                    user.setProjectUsers(new ProjectUserController(user).getProjectUserForPermissions(projectUserDto));
                    return user;
                }
                else{
                    throw new AqualityPermissionsException("Session Expired.", user);
                }
            }
        }
        throw new AqualityPermissionsException("Credentials you've provided are not valid. Reenter please.", user);
    }

    /**
     * Check If Token Valid
     * @param sessionId Session ID = project:{projectId}:{token}
     * @return Project Engineer
     * @throws AqualityException error about authorization status
     */
    private UserDto IsAuthorizedToken(String sessionId) throws AqualityException {
        String[] strings = sessionId.split(":");
        Integer projectId =  Integer.parseInt(strings[1]);
        boolean isTokenValid = new APITokenController(new UserDto()).isTokenValid(strings[2],projectId);

        if(isTokenValid){
            UserDto user = new UserDto();
            ProjectUserDto projectUser = new ProjectUserDto();
            projectUser.setProject_id(projectId);
            projectUser.setViewer(1);
            projectUser.setEngineer(1);
            user.setProjectUsers(Collections.singletonList(projectUser));
            user.setApiSessionProjectId(projectId);
            return user;
        }
        else{
            throw new AqualityPermissionsException("The API token is not valid", null);
        }
    }
}
