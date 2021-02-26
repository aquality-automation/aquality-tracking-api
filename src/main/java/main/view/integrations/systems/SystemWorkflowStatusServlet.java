package main.view.integrations.systems;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.systems.workflow.SystemWorkflowStatusDao;
import main.model.dto.integrations.systems.workflow.SystemWorkflowStatusDto;
import main.view.CrudServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/systems/workflow/status")
public class SystemWorkflowStatusServlet extends CrudServlet<SystemWorkflowStatusDto, SystemWorkflowStatusDao> {

    public SystemWorkflowStatusServlet() {
        super(ControllerType.SYSTEM_WORKFLOW_STATUS_CONTROLLER);
    }
}
