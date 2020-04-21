package tests.workers.imports.TestNG;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.JavaJUnitTestNG;
import main.model.db.imports.TestNameNodeType;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class ClassNameTestNGHandlerTest implements IHandlerTest {

    private JavaJUnitTestNG javaJUnitTestNGHandler;

    @Override
    public Handler getHandler() {
        return javaJUnitTestNGHandler;
    }

    @Override
    public String getReportPath() {
        return "reports/TestNG/";
    }

    @Override
    public String getTestFileName() {
        return "testWithClassName.json";
    }

    @BeforeMethod
    @Override
    public void tryParse() {
        try {
            javaJUnitTestNGHandler = new JavaJUnitTestNG(FileUtils.getResourceFile(getFilePath("TEST-TestSuite.xml")), TestNameNodeType.className, getFinishTime());
        } catch (Exception e) {
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }
}
