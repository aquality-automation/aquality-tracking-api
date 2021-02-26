package main.utils.integrations.atlassian.xray;

import main.utils.integrations.ITestTrackingApi;
import main.utils.integrations.atlassian.RestClientResponse;
import main.utils.integrations.atlassian.jira.JiraHttpClient;
import main.utils.integrations.atlassian.xray.models.AddTestResultResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.List;

/**
 * https://confluence.xpand-addons.com/display/XRAY/REST+API
 */
public class XrayApi extends JiraHttpClient implements ITestTrackingApi {

    public XrayApi(String url, String username, String password) {
        super(url, username, password);
    }

    public int addTestExecTestResult(String testExecutionKey, String testKey, int statusId) throws IOException {
        StringEntity postingString = new StringEntity(String.valueOf(statusId));
        HttpPost httpPost = new HttpPost(getUrl("/testexec/" + testExecutionKey + "/execute/" + testKey));
        httpPost.setEntity(postingString);
        RestClientResponse response = executePost(httpPost);
        return objectMapper.readValue(response.getBody(), AddTestResultResponse.class).getObj().getTest().getEntityValues().getEntityId();
    }

    public void addDefectToTestResult(String issueKey, int testResultId) throws IOException {
        StringEntity postingString = new StringEntity("[\"" + issueKey + "\"]");
        HttpPost httpPost = new HttpPost(getUrl("/testrun/" + testResultId + "/defects"));
        httpPost.setEntity(postingString);
        executePost(httpPost);
    }

    public void addTestToTestExecution(String testExecutionKey, List<String> testKeys) throws IOException {
        String keysString = listToQuotedStrings(testKeys);
        StringEntity postingString = new StringEntity(String.format("{\"keys\":[%1$s]}", keysString));
        HttpPost httpPost = new HttpPost(getUrl("/testexec/" + testExecutionKey + "/test"));
        httpPost.setEntity(postingString);
        executePost(httpPost);
    }

    private RestClientResponse executePost(HttpPost httpPost) throws IOException {
        httpPost.setHeader("Content-Type", "application/json");
        return send(httpPost);
    }

    private String getUrl(String path) {
        return getUrl() + "/rest/raven/1.0" + path;
    }
}
