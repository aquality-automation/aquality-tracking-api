package main.model.db.dao.customer;

import main.model.db.dao.DAO;
import main.model.dto.CustomerDto;

public class CustomerDao extends DAO<CustomerDto>{

    public CustomerDao() {
        super(CustomerDto.class);
        select = "{call SELECT_CUSTOMER(?)}";
        remove = "{call REMOVE_CUSTOMER(?)}";
        insert = "{call INSERT_CUSTOMER(?,?,?)}";
    }
}
