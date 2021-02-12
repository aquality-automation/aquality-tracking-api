package main.view.integrations.systems;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.systems.SystemTypeDao;
import main.model.dto.integrations.systems.SystemTypeDto;
import main.view.GetOnlyServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/system/types")
public class SystemTypesServlet extends GetOnlyServlet<SystemTypeDto, SystemTypeDao> {

    public SystemTypesServlet() {
        super(ControllerType.SYSTEM_TYPE_CONTROLLER);
    }
}
