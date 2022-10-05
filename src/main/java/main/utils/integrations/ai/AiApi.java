package main.utils.integrations.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.utils.integrations.ai.models.AdditionalProp;
import main.utils.integrations.ai.models.AiIssues;
import main.utils.integrations.atlassian.RestClientResponse;
import main.utils.integrations.atlassian.jira.JiraHttpClient;
import main.utils.integrations.atlassian.xray.models.AddTestResultResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * https://confluence.xpand-addons.com/display/XRAY/REST+API
 */
public class AiApi {

    protected final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(JiraHttpClient.class.getName());
    private final String url;
    private CloseableHttpClient client;

    public AiApi() {
        url = null;
        client = HttpClients.createDefault();

    }

    protected String getUrl() {
        return url;
    }

    public List<AdditionalProp> getIssues(String projectId) throws IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(getUrl("/issues"));
        URI uri = new URIBuilder(httpGet.getURI())
                .addParameter("id", projectId)
                .build();
        httpGet.setURI(uri);
        RestClientResponse response = executeGet(httpGet);
        return objectMapper.readValue(response.getBody(), AiIssues.class).getAdditionalProp();
    }

    public List<AdditionalProp> postIssues(AiIssues aiIssues) throws IOException, URISyntaxException {
        HttpPost httpPost = new HttpPost(getUrl("/issues"));
        URI uri = new URIBuilder(httpPost.getURI())
                .build();
        httpPost.setURI(uri);
        httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(aiIssues)));
        RestClientResponse response = executePost(httpPost);
        return response.getStatusCode() == HttpStatus.SC_OK ? objectMapper.readValue(response.getBody(), AiIssues.class).getAdditionalProp() : Collections.emptyList();
    }

    public List<AdditionalProp> deleteIssue(List<Integer> listIds) throws IOException, URISyntaxException {
        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(getUrl("/issues"));
        URI uri = new URIBuilder(httpDeleteWithBody.getURI())
                .build();
        httpDeleteWithBody.setURI(uri);
        httpDeleteWithBody.setEntity(new StringEntity(objectMapper.writeValueAsString(listIds)));
        RestClientResponse response = executeDelete(httpDeleteWithBody);
        return objectMapper.readValue(response.getBody(), AiIssues.class).getAdditionalProp();
    }

    private RestClientResponse executeDelete(HttpDeleteWithBody httpDeleteWithBody) throws IOException {
        httpDeleteWithBody.setHeader("Content-Type", "application/json");
        return send(httpDeleteWithBody);
    }

    private RestClientResponse executePost(HttpPost httpPost) throws IOException {
        httpPost.setHeader("Content-Type", "application/json");
        return send(httpPost);
    }

    private RestClientResponse executeGet(HttpGet httpGet) throws IOException {
        return send(httpGet);
    }

    protected RestClientResponse send(HttpRequestBase httpRequestBase) throws IOException {
        List<RestClientResponse> restClientResponses = new ArrayList<>();
        RestClientResponse restClientResponse;
        try {
            restClientResponse = execute(httpRequestBase);
        } catch (IOException e) {
            logger.warning("IO exception during JIRA " + httpRequestBase.getMethod().toUpperCase() + " request");
            throw e;
        }
        restClientResponses.add(restClientResponse);
        return restClientResponses.get(0);
    }

    private RestClientResponse execute(HttpRequestBase request) throws IOException {
        HttpResponse response = client.execute(request);
        StringBuilder result = new StringBuilder();
        if (!getListOfNoContentCodes().contains(response.getStatusLine().getStatusCode())) {
            result = getParsedResult(response);
        }
        return new RestClientResponse(request.getURI().toString(),
                result.toString(), response.getStatusLine().getStatusCode(), response.getAllHeaders());
    }

    private String getUrl(String path) {
        return getUrl() + path;
    }

    private List<Integer> getListOfNoContentCodes() {
        return Collections.singletonList(HttpStatus.SC_NO_CONTENT);
    }

    private StringBuilder getParsedResult(HttpResponse response) throws IOException {
        String charsetName = "UTF-8";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charsetName))) {
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Header[] headers = response.getAllHeaders();
            for (Header respHeader : headers) {
                logger.info(respHeader.getName() + ": " + respHeader.getValue());
            }
            logger.info("Status Code : " + response.getStatusLine().getStatusCode());
            logger.info("Content: " + result);
            return result;
        }
    }
}
