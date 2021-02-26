package main.view.integrations.tts;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.tts.TestTrackingTypeDao;
import main.model.dto.integrations.tts.TestTrackingTypeDto;
import main.view.GetOnlyServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/tts/types")
public class TestTrackingTypesServlet extends GetOnlyServlet<TestTrackingTypeDto, TestTrackingTypeDao> {

    public TestTrackingTypesServlet() {
        super(ControllerType.TTS_TYPE_CONTROLLER);
    }
}
