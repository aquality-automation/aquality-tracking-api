package tests.workers.imports.cucumber;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.Cucumber;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class CucumberHandlerTest implements IHandlerTest {

    private Cucumber cucumber;

    @Override
    public Handler getHandler() {
        return cucumber;
    }

    @Override
    public String getReportPath() {
        return "reports/cucumber/";
    }

    @Override
    public String getTestFileName() {
        return "tests.json";
    }

    @BeforeMethod
    @Override
    public void tryParse() {
        try {
            cucumber = new Cucumber(FileUtils.getResourceFile(getFilePath("cucumber.json")), getFinishTime());
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }
}
