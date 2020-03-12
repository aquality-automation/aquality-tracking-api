package main.model.db.dao.settings;

import main.model.db.dao.DAO;
import main.model.dto.settings.LdapDto;

public class LdapDao extends DAO<LdapDto>{
    public LdapDao() {
        super(LdapDto.class);
        select = "{call SELECT_LDAP_SETTING(?)}";
        insert = "{call INSERT_LDAP_SETTING(?,?,?,?,?,?,?,?,?,?,?,?)}";
    }
}
