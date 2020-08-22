package tests.workers.imports.surefire;

import main.model.db.imports.TestNameNodeType;

public class TestNameMavenSurefireOnJunitHandlerTest extends AMavenSurefireHandlerTest {

    public TestNameMavenSurefireOnJunitHandlerTest() {
        super(TestNameNodeType.testName, "TEST-JustTest.xml", "testWithTestName.json", "reports/JUnit/");
    }
}
