package main.model.db.imports.ImportHandlers;

import main.exceptions.RPException;
import main.model.db.imports.Handler;
import main.model.db.imports.SAXHandlers.PHPCodeceptionHandler;
import main.model.dto.TestDto;
import main.model.dto.TestResultDto;
import main.model.dto.TestRunDto;
import main.model.dto.TestSuiteDto;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PHPCodeception extends Handler {

    private PHPCodeceptionHandler handler;

    public PHPCodeception(File file) throws RPException {
        handler = new PHPCodeceptionHandler();
        try {
            this.parser.parse(file, handler);
        } catch (SAXException | IOException e) {
            throw new RPException("Cannot Parse Codeception file");
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
