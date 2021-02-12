package main.model.db.dao.integrations.systems.workflow;

import main.model.db.dao.DAO;
import main.model.dto.integrations.systems.workflow.SystemWorkflowStatusTypeDto;

public class SystemWorkflowStatusTypeDao extends DAO<SystemWorkflowStatusTypeDto> {

    public SystemWorkflowStatusTypeDao() {
        super(SystemWorkflowStatusTypeDto.class);
        select = "{call SELECT_INT_SYSTEM_WORKFLOW_STATUS_TYPES()}";
    }
}
