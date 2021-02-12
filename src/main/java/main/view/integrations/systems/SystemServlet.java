package main.view.integrations.systems;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.systems.SystemDao;
import main.model.dto.integrations.systems.SystemDto;
import main.view.CrudServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/systems")
public class SystemServlet extends CrudServlet<SystemDto, SystemDao> {

    public SystemServlet() {
        super(ControllerType.SYSTEM_CONTROLLER);
    }
}
