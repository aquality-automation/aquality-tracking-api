package main.model.db.imports;

import main.exceptions.AqualityException;
import main.model.db.imports.ImportHandlers.*;

import java.io.File;

class HandlerFactory {
    Handler getHandler(File file, String type, TestNameNodeType testNameNodeType) throws AqualityException {
        switch (ImportTypes.valueOf(type)){
            case MSTest:
                if(testNameNodeType == null){
                    throw new AqualityException("testNameNode is required");
                }
                return new TRX(file, testNameNodeType);
            case Robot:
                return new Robot(file);
            case JUnit:
            case TestNG:
                if(testNameNodeType == null){
                    throw new AqualityException("testNameNode is required");
                }
                return new JavaTestNG(file, testNameNodeType);
            case Cucumber:
            case TestNGCucumber:
                return new Cucumber(file);
            case PHPCodeception:
                return new PHPCodeception(file);
            case NUnit_v2:
                return new NUnitV2(file);
            case NUnit_v3:
                if(testNameNodeType == null){
                    throw new AqualityException("testNameNode is required");
                }
                return new NUnitV3(file, testNameNodeType);
            default:
                throw new AqualityException(String.format("Import Type '%s' is not implemented", type));
        }
    }
}
