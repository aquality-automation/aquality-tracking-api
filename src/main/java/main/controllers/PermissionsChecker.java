package main.controllers;

import main.exceptions.AqualityPermissionsException;
import main.model.dto.UserDto;

public class PermissionsChecker {
    private UserDto baseUser;
    PermissionsChecker(UserDto user){
        baseUser = user;
    }

    public void checkAdmin(String error) throws AqualityPermissionsException {
        if(!baseUser.isAdmin()){
            throw new AqualityPermissionsException(error, baseUser);
        }
    }
}
