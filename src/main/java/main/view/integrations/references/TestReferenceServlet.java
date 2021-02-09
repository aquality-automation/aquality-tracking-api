package main.view.integrations.references;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.TestReferenceDto;
import main.view.CrudServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/references/test")
public class TestReferenceServlet extends CrudServlet<TestReferenceDto, ReferenceDao<TestReferenceDto>> {

    public TestReferenceServlet() {
        super(ControllerType.REF_TEST_CONTROLLER);
    }
}
