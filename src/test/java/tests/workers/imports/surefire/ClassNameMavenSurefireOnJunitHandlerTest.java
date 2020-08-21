package tests.workers.imports.surefire;

import main.model.db.imports.TestNameNodeType;

public class ClassNameMavenSurefireOnJunitHandlerTest extends AMavenSurefireHandlerTest {

    public ClassNameMavenSurefireOnJunitHandlerTest() {
        super(TestNameNodeType.className, "TEST-JustTest.xml", "testWithClassName.json", "reports/JUnit/");
    }
}
