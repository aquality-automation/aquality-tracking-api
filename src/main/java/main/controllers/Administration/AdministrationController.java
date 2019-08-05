package main.controllers.Administration;

import main.controllers.Administration.UserController;
import main.exceptions.RPException;
import main.model.dto.UserDto;

public class AdministrationController {
    private UserController userController;
    public AdministrationController(UserDto user) {
        userController = new UserController(user);
    }

    /**
     * Try to authorize with authorization string
     * @param authString authorization string
     * @param ldap do authorization with LDAP or not
     * @return User
     */
    public UserDto auth(String authString, boolean ldap) throws RPException {
        return userController.auth(authString, ldap);
    }
}
