package tests.workers.imports.codeception;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.PHPCodeception;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class CodecepionHandlerTest implements IHandlerTest {

    private PHPCodeception phpCodeception;

    @Override
    public Handler getHandler() {
        return phpCodeception;
    }

    @Override
    public String getReportPath() {
        return "reports/codeception/";
    }

    @Override
    public String getTestFileName() {
        return "tests.json";
    }

    @BeforeMethod
    @Override
    public void tryParse() {
        try {
            phpCodeception = new PHPCodeception(FileUtils.getResourceFile(getFilePath("codeception.xml")), getFinishTime());
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }
}
