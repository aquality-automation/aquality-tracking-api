package main.model.db.dao.integrations.systems.workflow;

import main.model.db.dao.DAO;
import main.model.dto.integrations.systems.workflow.SystemWorkflowStatusDto;

public class SystemWorkflowStatusDao extends DAO<SystemWorkflowStatusDto> {

    public SystemWorkflowStatusDao() {
        super(SystemWorkflowStatusDto.class);
        select = "{call SELECT_INT_SYSTEM_WORKFLOW_STATUS(?,?,?,?,?)}";
        insert = "{call INSERT_INT_SYSTEM_WORKFLOW_STATUS(?,?,?,?,?)}";
        remove = "{call REMOVE_INT_SYSTEM_WORKFLOW_STATUS(?)}";
    }
}
