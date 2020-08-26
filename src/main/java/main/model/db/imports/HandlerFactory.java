package main.model.db.imports;

import main.exceptions.AqualityException;
import main.model.db.imports.ImportHandlers.*;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class HandlerFactory {

    Handler getHandler(File file, String type, TestNameNodeType testNameNodeType, Date finishTime) throws AqualityException {
        validateTypeOnNameNodeRequirements(ImportTypes.valueOf(type), testNameNodeType);

        switch (ImportTypes.valueOf(type)) {
            case MSTest:
                return new TRX(file, testNameNodeType);
            case Robot:
                return new Robot(file);
            case JUnit:
            case TestNG:
            case MavenSurefire:
                return new MavenSurefireHandler(file, testNameNodeType, finishTime);
            case Cucumber:
            case TestNGCucumber:
                return new Cucumber(file, finishTime);
            case PHPCodeception:
                return new PHPCodeception(file, finishTime);
            case NUnit_v2:
                return new NUnitV2(file);
            case NUnit_v3:
                return new NUnitV3(file, testNameNodeType);
            default:
                throw new AqualityException(String.format("Import type '%s' is not implemented", type));
        }
    }

    private void validateTypeOnNameNodeRequirements(ImportTypes type, TestNameNodeType nodeType) throws AqualityException {
        List<ImportTypes> nameNodeRequiredTypes = Arrays.asList(
                ImportTypes.MSTest,
                ImportTypes.JUnit,
                ImportTypes.TestNG,
                ImportTypes.MavenSurefire,
                ImportTypes.NUnit_v3);
        if (nameNodeRequiredTypes.contains(type) && nodeType == null) {
            throw new AqualityException(String.format("While you are using import type '%1$s' the TestNameNodeType is required. Allowed values: %2$s",
                    type,
                    Arrays.stream(TestNameNodeType.values())
                            .map(TestNameNodeType::toString)
                            .collect(Collectors.joining(","))));
        }
    }
}
