package tests.workers.imports.surefire;

import main.model.db.imports.TestNameNodeType;

public class TestNameMavenSurefireOnTestNGHandlerTest extends AMavenSurefireHandlerTest {
    public TestNameMavenSurefireOnTestNGHandlerTest() {
        super(TestNameNodeType.testName, "TEST-TestSuite.xml", "testWithTestName.json", "reports/TestNG/");
    }
}
