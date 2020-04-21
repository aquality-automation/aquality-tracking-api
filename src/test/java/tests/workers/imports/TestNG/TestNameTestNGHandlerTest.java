package tests.workers.imports.TestNG;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.JavaJUnitTestNG;
import main.model.db.imports.TestNameNodeType;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class TestNameTestNGHandlerTest implements IHandlerTest {
    private JavaJUnitTestNG javaJUnitTestNG;

    @Override
    public Handler getHandler() {
        return javaJUnitTestNG;
    }

    @Override
    public String getReportPath() {
        return "reports/TestNG/";
    }

    @Override
    public String getTestFileName() {
        return "testWithTestName.json";
    }

    @BeforeMethod
    public void tryParse() {
        try {
            javaJUnitTestNG = new JavaJUnitTestNG(FileUtils.getResourceFile(getFilePath("TEST-TestSuite.xml")), TestNameNodeType.testName, getFinishTime());
        } catch (Exception e) {
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }
}
