package main.view.integrations;

import main.Session;
import main.controllers.ControllerType;
import main.exceptions.AqualityException;
import main.model.dto.integrations.systems.SystemDto;

import java.util.List;

public class SystemProvider {

    private SystemProvider(){
        // provider class. should include static methods only
    }

    public static SystemDto getSystem(Session session, int projectId, int intSystemId) throws AqualityException {
        SystemDto systemDto = new SystemDto();
        systemDto.setProject_id(projectId);
        systemDto.setId(intSystemId);
        List<SystemDto> systems = session.controllerFactory.getProjectEntityHandler(ControllerType.SYSTEM_CONTROLLER).get(systemDto);
        if(systems.isEmpty()){
            throw new AqualityException(String.format("System with id %1$s is not defined in the project %2$s", intSystemId, projectId));
        }
        return systems.get(0);
    }
}
