package main.view.integrations.references;

import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.integrations.references.TestRunReferenceDto;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/references/testrun")
public class TestRunReferenceServlet extends ReferenceServlet<TestRunReferenceDto> {

    public TestRunReferenceServlet() {
        super(ReferenceType.TEST_RUN);
    }
}
