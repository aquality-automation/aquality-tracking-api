package main.view.integrations.references;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.TestRunReferenceDto;
import main.view.CrudServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/references/testrun")
public class TestRunReferenceServlet extends CrudServlet<TestRunReferenceDto, ReferenceDao<TestRunReferenceDto>> {

    public TestRunReferenceServlet() {
        super(ControllerType.REF_TESTRUN_CONTROLLER);
    }
}
