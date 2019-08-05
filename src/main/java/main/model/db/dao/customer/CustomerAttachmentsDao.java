package main.model.db.dao.customer;

import main.model.db.dao.DAO;
import main.model.dto.CustomerAttachmentDto;

public class CustomerAttachmentsDao extends DAO<CustomerAttachmentDto>{
    public CustomerAttachmentsDao() {
        super(CustomerAttachmentDto.class);

        insert = "{call INSERT_CUSTOMER_ATTACHMENT(?,?)}";
        remove = "{call REMOVE_CUSTOMER_ATTACHMENT(?)}";
        select = "{call SELECT_CUSTOMER_ATTACHMENT(?,?)}";
    }

}
