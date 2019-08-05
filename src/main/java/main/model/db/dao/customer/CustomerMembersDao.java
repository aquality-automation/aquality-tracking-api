package main.model.db.dao.customer;

import main.model.db.dao.DAO;
import main.model.dto.CustomerMemberDto;

public class CustomerMembersDao extends DAO<CustomerMemberDto> {
    public CustomerMembersDao() {
        super(CustomerMemberDto.class);

        insert = "{call INSERT_CUSTOMER_MEMBER(?,?)}";
        select = "{call SELECT_CUSTOMER_MEMBER(?,?)}";
        remove = "{call REMOVE_CUSTOMER_MEMBER(?)}";
    }
}
