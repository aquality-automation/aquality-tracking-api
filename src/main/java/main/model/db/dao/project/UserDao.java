package main.model.db.dao.project;

import main.exceptions.RPException;
import main.exceptions.RPPermissionsException;
import main.model.db.dao.DAO;
import main.model.dto.UserDto;
import main.utils.DateUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Date;
import java.util.List;


public class UserDao extends DAO<UserDto> {
    public UserDao() {
        super(UserDto.class);
        select = "{call SELECT_USERS(?,?,?,?,?,?,?)}";
        insert = "{call INSERT_USER(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_USER(?)}";
    }

    public UserDto IsAuthorized(String sessionId) throws RPException {
        Base64 base64 = new Base64();
        DateUtils dates = new DateUtils();
        String[] strings = StringUtils.newStringUtf8(base64.decode(sessionId)).split(":");
        UserDto user = new UserDto();
        user.setUser_name(strings[0]);
        List<UserDto> users = searchAll(user);
        if(users.size() > 0){
            user = users.get(0);
            if(!user.getSession_code().equals(sessionId)){
                throw new RPPermissionsException("Credentials you've provided are not valid. Reenter please.", user);
            }
            if(new Date().after(dates.fromyyyyMMdd(strings[2]))){
                throw new RPPermissionsException("Session Expired.", user);
            }
        }
        else{
            throw new RPPermissionsException("Credentials you've provided are not valid. Reenter please.", user);
        }
        return user;
    }
}
