package main.view.integrations.references;

import main.model.dto.integrations.references.IssueReferenceDto;
import main.model.dto.integrations.references.ReferenceType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/integration/references/issue")
public class IssueReferenceServlet extends ReferenceServlet<IssueReferenceDto> {

    public IssueReferenceServlet() {
        super(ReferenceType.ISSUE);
    }
}
