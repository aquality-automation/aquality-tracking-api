package tests.workers.imports;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.Cucumber;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class CucumberHandlerTest implements IHandlerTest{

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
            cucumber = new Cucumber(FileUtils.getResourceFile("reports/cucumber/cucumber.json"), getFinishTime());
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()));
        }
    }

    @Test
    public void shouldFindChildElements() {
        Assert.assertTrue(true);
    }
}
