package main.model.db.dao.customer;

import main.model.db.dao.DAO;
import main.model.dto.CustomerCommentDto;

public class CustomerCommentsDao extends DAO<CustomerCommentDto> {
    public CustomerCommentsDao() {
        super(CustomerCommentDto.class);
        insert = "{call INSERT_CUSTOMER_COMMENT(?,?,?,?)}";
        select = "{call SELECT_CUSTOMER_COMMENT(?,?)}";
    }
}
