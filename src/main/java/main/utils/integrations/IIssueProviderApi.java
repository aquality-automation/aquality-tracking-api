package main.utils.integrations;

import main.model.dto.integrations.references.RefStatus;

import java.io.IOException;
import java.util.List;

public interface IIssueProviderApi {

    List<RefStatus> getIssues(List<String> keys) throws IOException;
}
