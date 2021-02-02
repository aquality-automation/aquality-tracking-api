package main.view.integrations.references;

import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.integrations.references.TestReferenceDto;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/references/test")
public class TestReferenceServlet extends ReferenceServlet<TestReferenceDto> {

    public TestReferenceServlet() {
        super(ReferenceType.TEST);
    }
}
