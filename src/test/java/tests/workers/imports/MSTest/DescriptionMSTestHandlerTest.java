package tests.workers.imports.MSTest;

import main.model.db.imports.Handler;
import main.model.db.imports.ImportHandlers.TRX;
import main.model.db.imports.TestNameNodeType;
import org.testng.annotations.BeforeMethod;
import tests.workers.imports.IHandlerTest;
import utils.FileUtils;

import static org.testng.Assert.fail;

public class DescriptionMSTestHandlerTest implements IHandlerTest {
    private TRX trxImportHandler;

    @Override
    public Handler getHandler() {
        return trxImportHandler;
    }

    @Override
    public String getReportPath() {
        return "reports/MSTest/";
    }

    @Override
    public String getTestFileName() {
        return "testWithDescriptionName.json";
    }

    @BeforeMethod
    public void tryParse() {
        try {
            trxImportHandler = new TRX(FileUtils.getResourceFile(getFilePath("mstest.trx")), TestNameNodeType.descriptionNode);
        } catch (Exception e) {
            fail(String.format("Failed on Handler Creating: %s", e.getMessage()), e);
        }
    }
}
