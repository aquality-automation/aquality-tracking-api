package main.model.db.dao.audit;

import main.model.db.dao.DAO;
import main.model.dto.audit.ServiceDto;

public class ServiceDao extends DAO<ServiceDto> {
    public ServiceDao() {
        super(ServiceDto.class);
        select = "{call SELECT_SERVICE_TYPE(?)}";
    }
}
