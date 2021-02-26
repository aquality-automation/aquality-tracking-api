package main.view.integrations.references;

import main.controllers.ControllerType;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.dto.integrations.references.IssueReferenceDto;
import main.view.CrudServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/integration/references/issue")
public class IssueReferenceServlet extends CrudServlet<IssueReferenceDto, ReferenceDao<IssueReferenceDto>> {


    public IssueReferenceServlet() {
        super(ControllerType.REF_ISSUE_CONTROLLER);
    }
}
