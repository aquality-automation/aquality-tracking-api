package main.exceptions;

import main.model.dto.UserDto;

public class RPPermissionsException extends RPException {
    public RPPermissionsException(String error, UserDto user) {
        super(String.format("[Permissions %s]: " + error, user != null ? user.getUser_name() : "anonymous"));
        this.responseCode = 403;
    }
}
