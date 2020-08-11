package tests.workers.imports.JUnit;

import main.model.db.imports.TestNameNodeType;

public class ClassNameJUnitHandlerTest extends AJUnitHandlerTest {

    public ClassNameJUnitHandlerTest() {
        super(TestNameNodeType.className, "TEST-JustTest.xml", "testWithClassName.json");
    }
}
