package main.model.db.imports;

import main.model.db.imports.enums.TestNameNodeType;
import main.exceptions.RPException;
import main.model.db.imports.ImportHandlers.*;

import java.io.File;

class HandlerFactory {
    Handler getHandler(File file, String type, TestNameNodeType testNameNodeType) throws RPException {
        switch (type){
            case "MSTest":
                if(testNameNodeType == null){
                    throw new RPException("testNameNode is required");
                }
                return new TRX(file, testNameNodeType);
            case "Robot":
                return new Robot(file);
            case "TestNG":
                if(testNameNodeType == null){
                    throw new RPException("testNameNode is required");
                }
                return new JavaTestNG(file, testNameNodeType);
            case "Cucumber":
            case "TestNGCucumber":
                return new Cucumber(file);
            case "PHPCodeception":
                return new PHPCodeception(file);
            case "NUnit_v2":
                return new NUnitV2(file);
            case "NUnit_v3":
                return new NUnitV3(file);
            default:
                throw new RPException(String.format("Import Type '%s' is not implemented", type));
        }
    }
}
