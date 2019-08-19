package main.model.db.imports.ImportHandlers;

import main.exceptions.AqualityException;
import main.model.db.imports.Handler;
import main.model.db.imports.SAXHandlers.NUnitV3Handler;
import main.model.dto.TestDto;
import main.model.dto.TestResultDto;
import main.model.dto.TestRunDto;
import main.model.dto.TestSuiteDto;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NUnitV3 extends Handler {

    private NUnitV3Handler handler;

    public NUnitV3(File file) throws AqualityException {
        handler = new NUnitV3Handler();
        try {
            this.parser.parse(file, handler);
        } catch (SAXException | IOException e) {
            throw new AqualityException("Cannot Parse NunitV3 file");
        }
    }

    @Override
    public TestSuiteDto getTestSuite() {
        return handler.getTestSuite();
    }

    @Override
    public TestRunDto getTestRun() {
        return handler.getTestRun();
    }

    @Override
    public List<TestDto> getTests() {
        return handler.getTests();
    }

    @Override
    public List<TestResultDto> getTestResults() {
        return handler.getTestResults();
    }
}
