package main.utils.integrations.providers;

import main.model.dto.integrations.FixedType;
import main.model.dto.integrations.systems.SystemDto;
import main.model.dto.integrations.systems.SystemType;
import main.utils.integrations.IIssueProviderApi;
import main.utils.integrations.atlassian.jira.JiraHttpClient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IntSystemIssueFactory {

    private IntSystemIssueFactory() {
        // factory method class
    }

    public static IIssueProviderApi getIssueProvider(SystemDto system) {
        int typeId = system.getInt_system_type();
        FixedType type = FixedType.getType(Arrays.stream(SystemType.values()).map(SystemType::getType).collect(Collectors.toList()), typeId);
        if (type.getId() == SystemType.JIRA.getType().getId()) {
            String url = system.getUrl();
            String username = system.getUsername();
            String password = system.getPassword();
            return new JiraHttpClient(url, username, password);
        }
        throw new IllegalArgumentException("System Type Id " + typeId + " is not supported. Following type are supported: " + Arrays.toString(SystemType.values()));
    }
}
