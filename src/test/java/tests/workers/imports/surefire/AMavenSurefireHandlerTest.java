package tests.workers.imports.surefire;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.MavenSurefireHandler;
import main.model.db.imports.TestNameNodeType;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public abstract class AMavenSurefireHandlerTest implements IHandlerTest {
    private MavenSurefireHandler mavenSurefireHandler;
    private final String actualFileName;
    private final String resultsFileName;
    private final TestNameNodeType type;
    private final String reportPath;

    AMavenSurefireHandlerTest(TestNameNodeType type, String actualFileName, String expectedFileName, String reportPath) {
        this.type = type;
        this.actualFileName = actualFileName;
        this.resultsFileName = expectedFileName;
        this.reportPath = reportPath;
    }

    @Override
    public Handler getHandler() {
        return mavenSurefireHandler;
    }

    @Override
    public String getReportPath() {
        return reportPath;
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
