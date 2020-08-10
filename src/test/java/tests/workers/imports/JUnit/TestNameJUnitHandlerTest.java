package tests.workers.imports.JUnit;

import main.model.db.imports.TestNameNodeType;

public class TestNameJUnitHandlerTest extends AJUnitHandlerTest {

    public TestNameJUnitHandlerTest() {
        super(TestNameNodeType.testName, "TEST-JustTest.xml", "testWithTestName.json");
    }
}
