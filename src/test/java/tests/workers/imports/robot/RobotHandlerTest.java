package tests.workers.imports.robot;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.PHPCodeception;
import main.model.db.imports.ImportHandlers.Robot;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class RobotHandlerTest implements IHandlerTest {

    private Robot robot;

    @Override
    public Handler getHandler() {
        return robot;
    }

    @Override
    public String getReportPath() {
        return "reports/robot/";
    }

    @Override
    public String getTestFileName() {
        return "tests.json";
    }

    @BeforeMethod
    @Override
    public void tryParse() {
        try {
            robot = new Robot(FileUtils.getResourceFile(getFilePath("robot.xml")));
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }
}
