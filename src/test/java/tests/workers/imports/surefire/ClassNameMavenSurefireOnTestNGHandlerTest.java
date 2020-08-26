package tests.workers.imports.surefire;

import main.model.db.imports.TestNameNodeType;

public class ClassNameMavenSurefireOnTestNGHandlerTest extends AMavenSurefireHandlerTest {

    public ClassNameMavenSurefireOnTestNGHandlerTest() {
        super(TestNameNodeType.className, "TEST-TestSuite.xml", "testWithClassName.json", "reports/TestNG/");
    }
}
