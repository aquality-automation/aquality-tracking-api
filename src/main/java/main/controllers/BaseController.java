package main.controllers;

import main.model.dto.BaseDto;
import main.model.dto.UserDto;

public abstract class BaseController<T extends BaseDto> implements IController<T>{
    protected UserDto baseUser;
    protected PermissionsChecker permissionsChecker;

    protected BaseController(UserDto user) {
        baseUser = user;
        permissionsChecker = new PermissionsChecker(baseUser);
    }
}
