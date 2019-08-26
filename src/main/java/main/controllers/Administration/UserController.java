package main.controllers.Administration;

import com.mysql.cj.core.conf.url.ConnectionUrlParser;
import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.PasswordDao;
import main.model.db.dao.project.UserDao;
import main.model.db.dao.project.UserSessionDao;
import main.model.dto.PasswordDto;
import main.model.dto.UserDto;
import main.model.dto.UserSessionDto;
import main.utils.DateUtils;
import main.utils.LDAP.LDAPAuthenticator;
import main.utils.RSA.RSAUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserController extends BaseController<UserDto> {
    private UserDao userDao;
    private PasswordDao passwordDao;
    private UserSessionDao userSessionDao;

    public UserController(UserDto user) {
        super(user);
        userDao = new UserDao();
        passwordDao = new PasswordDao();
        userSessionDao = new UserSessionDao();
    }

    @Override
    public UserDto create(UserDto template) throws AqualityException {
        if(baseUser.isAdmin() || baseUser.getId().equals(template.getId())){
            if(template.getPassword() != null){
                template.setPassword(saltPassword(template, template.getPassword()));
            }
            return userDao.create(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create User", baseUser);
        }
    }

    @Override
    public List<UserDto> get(UserDto template) throws AqualityException {
        return toPublicUsers(userDao.searchAll(template));
    }

    @Override
    public boolean delete(UserDto template) throws AqualityException {
        if(baseUser.isAdmin()){
            return userDao.delete(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to delete User", baseUser);
        }
    }

    public UserDto updatePassword(PasswordDto password) throws AqualityException {
        UserDto user = new UserDto();
        user.setId(password.getUser_id());
        user = get(user).get(0);

        password.setOld_password(saltPassword(user, password.getOld_password()));
        password.setPassword(saltPassword(user, password.getPassword()));
        passwordDao.create(password);

        user = get(user).get(0);
        user.setSession_code(generateSessionCode(user));
        return userDao.create(user);
    }

    public UserDto auth(String authString, boolean ldap) throws AqualityException {
        Base64 base64= new Base64();
        String authStringDecoded = StringUtils.newStringUtf8(base64.decode(authString));
        String[] authStringSplit = authStringDecoded.split(":");
        ConnectionUrlParser.Pair<String, PrivateKey> privateKeyPair
                = RSAUtil.keystore.stream().filter(x -> Objects.equals(x.left, authStringSplit[0])).findFirst().orElse(null);
        PrivateKey key = Objects.requireNonNull(privateKeyPair).right;
        String password;
        try {
            password = RSAUtil.getDecrypted(authStringSplit[1], key);
        } catch (Exception e) {
            throw new AqualityException("Password Decryption Error");
        } finally {
            RSAUtil.keystore.removeIf(x -> Objects.equals(x.left, authStringSplit[0]));
        }

        UserDto user = ldap ? handleLDAPAuthorization(authStringSplit[0], password) : checkUser(authStringSplit[0], password);

        if(user != null){
            user.setSession_code(generateSessionCode(user));
            updateSession(user);
            return user;
        }

        throw new AqualityException("Seems your password was updated. Log in again please.");
    }

    private List<UserDto> toPublicUsers(List<UserDto> users) {
        for (UserDto user :
                users) {
            user.setPassword("");
            user.setSession_code("");
        }
        return users;
    }

    private String generateSessionCode(UserDto user) {
        Base64 base64= new Base64();
        DateUtils dates = new DateUtils();
        String encode = null;
        try {
            encode = base64.encodeToString((user.getUser_name() + ":" + UUID.randomUUID().toString() + ":" + dates.toyyyyMMdd(dates.addDays(new Date(), 1))).getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    private String saltPassword(UserDto user, String password){
        String passHash = DigestUtils.md5Hex(password);
        return DigestUtils.md5Hex(user.getEmail()+passHash+"kjr1fdd00das");
    }

    private UserDto checkUser(String user_name, String password) throws AqualityException {
        UserDto user = new UserDto();
        user.setUser_name(user_name);
        List<UserDto> users = userDao.searchAll(user);

        if(users.size() > 0){
            user = users.get(0);
            String correctHex = user.getPassword();
            String actualHex = saltPassword(user, password);
            if(correctHex.equals(actualHex)){
                return user;
            }
        }

        throw new AqualityException("Credentials you've provided are not valid. Reenter please.");
    }

    private UserDto handleLDAPAuthorization(String userName, String password) throws AqualityException {
        LDAPAuthenticator ldap = new LDAPAuthenticator();
        UserDto user;
        user = ldap.tryAuthWithLdap(userName, password);
        if(user != null){
            UserDto templateUser = new UserDto();
            templateUser.setUser_name(user.getUser_name());
            List<UserDto> users = get(templateUser);

            if(users.size() > 0){
                user.setId(users.get(0).getId());
            }

            String saltPass = saltPassword(user, password);
            user.setPassword(saltPass);
            user.setLdap_user(1);
            user = create(user);
            user.setSession_code(generateSessionCode(user));
            user = create(user);
        }
        return user;
    }

    private void updateSession(UserDto user) throws AqualityException {
        UserSessionDto update = new UserSessionDto();
        update.setUser_id(user.getId());
        update.setSession_code(user.getSession_code());
        userSessionDao.create(update);
    }
}
