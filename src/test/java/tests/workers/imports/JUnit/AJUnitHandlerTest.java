package tests.workers.imports.JUnit;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.MavenSurefireHandler;
import main.model.db.imports.TestNameNodeType;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public abstract class AJUnitHandlerTest implements IHandlerTest {
    private MavenSurefireHandler mavenSurefireHandler;
    private final String actualFileName;
    private final String resultsFileName;
    private final TestNameNodeType type;

    AJUnitHandlerTest(TestNameNodeType type, String actualFileName, String expectedFileName) {
        this.type = type;
        this.actualFileName = actualFileName;
        this.resultsFileName = expectedFileName;
    }

    @Override
    public Handler getHandler() {
        return mavenSurefireHandler;
    }

    @Override
    public String getReportPath() {
        return "reports/JUnit/";
    }

    @Override
    public String getTestFileName() {
        return resultsFileName;
    }

    @BeforeMethod
    public void tryParse() {
        try {
            mavenSurefireHandler = new MavenSurefireHandler(FileUtils.getResourceFile(getFilePath(actualFileName)), type, getFinishTime());
        } catch (Exception e) {
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }
}