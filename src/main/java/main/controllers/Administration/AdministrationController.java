package main.controllers.Administration;

import main.exceptions.AqualityException;
import main.model.dto.settings.UserDto;

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
    public UserDto auth(String authString, boolean ldap) throws AqualityException {
        return userController.auth(authString, ldap);
    }
}
