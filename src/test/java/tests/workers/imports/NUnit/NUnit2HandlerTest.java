package tests.workers.imports.NUnit;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.NUnitV2;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class NUnit2HandlerTest implements IHandlerTest {
    private NUnitV2 nUnitV2;

    @Override
    public Handler getHandler() {
        return nUnitV2;
    }

    @Override
    public String getReportPath() {
        return "reports/NUnit2/";
    }

    @Override
    public String getTestFileName() {
        return "tests.json";
    }

    @BeforeMethod
    public void tryParse(){
        try {
            nUnitV2 = new NUnitV2(FileUtils.getResourceFile(getFilePath("NUnit2.xml")));
        } catch (Exception e){
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }
}
