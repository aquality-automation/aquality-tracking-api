package main.utils.LDAP;

import main.controllers.Administration.AppSettingsController;
import main.exceptions.AqualityException;
import main.model.dto.settings.LdapDto;
import main.model.dto.settings.UserDto;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;

public class LDAPAuthenticator {

    private LdapDto ldapDto = new LdapDto();

    public LDAPAuthenticator() throws AqualityException {
        UserDto user = new UserDto();
        user.setAdmin(1);
        AppSettingsController settingsController = new AppSettingsController(user);
        ldapDto = settingsController.getLdap();
        ldapDto.setAdminSecret(settingsController.getAdminSecret());
    }

    public UserDto tryAuthWithLdap(String userName, String password) throws AqualityException {
        if((ldapDto.getDomain() != null || !Objects.equals(ldapDto.getDomain(), "")) && !userName.contains("@")){
            userName = String.format("%s@%s", userName, ldapDto.getDomain());
        }
        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.SECURITY_AUTHENTICATION, ldapDto.getSecurity_auntification());
        env.put(Context.SECURITY_PRINCIPAL, ldapDto.getAdminUserName());
        env.put(Context.SECURITY_CREDENTIALS, ldapDto.getAdminSecret());
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapDto.getLdapAdServer());

        InitialDirContext context;

        try{
            context = new InitialDirContext(env);
        } catch (NamingException e){
            throw new AqualityException("LDAP admin credentials are wrong!");
        }

        SearchControls ctrls = new SearchControls();
        ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> answers;
        SearchResult result;
        try {
            answers = context.search(ldapDto.getLdapSearchBaseUsers(), String.format(ldapDto.getUserSearchFilter(), userName.split("@")[0]), ctrls);
            result = answers.nextElement();
        } catch (NamingException|NullPointerException e){
            throw new AqualityException("User is not found in LDAP!");
        }
        Properties props = new Properties();
        env.put(Context.SECURITY_AUTHENTICATION, ldapDto.getSecurity_auntification());
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapDto.getLdapAdServer());
        try {
            props.put(Context.SECURITY_PRINCIPAL, result.getAttributes().get("userprincipalname").get().toString()); }
        catch(NamingException e){
            throw new AqualityException("User principal name is missed!");
        }

        props.put(Context.SECURITY_CREDENTIALS, password);

        try{
            new InitialDirContext(props);
        } catch (NamingException e) {
            throw new AqualityException("LDAP refused your credentials!");
        }
        try {
            return findAccountByAccountName(result);
        } catch (Exception e){
            throw new AqualityException("Cannot fill user with Info from LDAP!" + e.getMessage());
        }
    }

    private UserDto findAccountByAccountName(SearchResult result) throws NamingException {
        Attributes attributes = result.getAttributes();
        UserDto user = new UserDto();
        try {
            user.setUser_name(attributes.get(ldapDto.getUserNameAttribute()).get().toString());
        } catch (Exception e){
            throw new AuthenticationException("Cannot fill user name with Info from LDAP!");
        }
        try {
            user.setEmail(attributes.get(ldapDto.getMailAttribute()).get().toString());
        } catch (Exception e){
            throw new AuthenticationException("Cannot fill user email with Info from LDAP!");
        }
        try {
            user.setFirst_name(attributes.get(ldapDto.getFirstNameAttribute()).get().toString());
        } catch (Exception e){
            throw new AuthenticationException("Cannot fill user first name with Info from LDAP!");
        }
        try {
            user.setSecond_name(attributes.get(ldapDto.getLastNameAttribute()).get().toString());
        } catch (Exception e){
            throw new AuthenticationException("Cannot fill user second name with Info from LDAP!");
        }

        return user;
    }
}
