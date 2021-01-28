package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.IntegrationSystemDto;

public class IntegrationSystemDao extends DAO<IntegrationSystemDto> {

    public IntegrationSystemDao(){
        super(IntegrationSystemDto.class);
        select = "{call SELECT_INTEGRATION_SYSTEMS()}";
    }
}
