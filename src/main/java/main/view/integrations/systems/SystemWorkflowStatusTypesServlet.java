package main.view.integrations.systems;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.systems.workflow.SystemWorkflowStatusTypeDao;
import main.model.dto.integrations.systems.workflow.SystemWorkflowStatusTypeDto;
import main.view.GetOnlyServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/system/workflow/status/types")
public class SystemWorkflowStatusTypesServlet extends GetOnlyServlet<SystemWorkflowStatusTypeDto, SystemWorkflowStatusTypeDao> {

    public SystemWorkflowStatusTypesServlet() {
        super(ControllerType.SYSTEM_WORKFLOW_STATUS_TYPE_CONTROLLER);
    }
}
