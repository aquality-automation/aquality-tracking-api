package main.view.integrations.tts;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.tts.TestTrackingStatusDao;
import main.model.dto.integrations.tts.TestTrackingStatusDto;
import main.view.CrudPerProjectServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/tts/status")
public class TestTrackingStatusServlet extends CrudPerProjectServlet<TestTrackingStatusDto, TestTrackingStatusDao> {

    public TestTrackingStatusServlet() {
        super(ControllerType.TTS_STATUS_CONTROLLER);
    }
}
