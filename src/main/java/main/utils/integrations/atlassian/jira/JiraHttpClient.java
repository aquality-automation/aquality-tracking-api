package main.utils.integrations.atlassian.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.dto.integrations.references.RefStatus;
import main.utils.integrations.IIssueProviderApi;
import main.utils.integrations.atlassian.HttpsTrustManager;
import main.utils.integrations.atlassian.RestClientResponse;
import main.utils.integrations.atlassian.jira.models.SearchResponse;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JiraHttpClient implements IIssueProviderApi {

    private final Logger logger = Logger.getLogger(JiraHttpClient.class.getName());
    private CloseableHttpClient client;
    private CookieStore httpCookieStore = new BasicCookieStore();
    private String authCookies = "";
    private final String url;
    private final String username;
    private final String password;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    public JiraHttpClient(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        client = prepareClientBuilderWithDefaultSSL().build();
    }

    private HttpClientBuilder prepareClientBuilderWithDefaultSSL() {
        SSLContext sslcontext;
        HttpClientBuilder clientBuilder = null;
        try {
            sslcontext = SSLContexts.custom().useSSL().build();
            sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
            clientBuilder = prepareClientBuilder(sslcontext);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Cannot build http client for Jira: " + e.getMessage());
        }
        return clientBuilder;
    }

    private HttpClientBuilder prepareClientBuilder(SSLContext sslcontext) {
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return HttpClients.custom()
                .setSSLSocketFactory(factory)
                .setDefaultCookieStore(httpCookieStore);
    }

    protected String getUrl() {
        return url;
    }

    protected RestClientResponse send(HttpRequestBase httpRequestBase) throws IOException {
        httpRequestBase.setHeader("Cookie", getAuthCookies());
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

    private String getAuthCookies() throws IOException {
        if (authCookies.equals("")) {
            HttpPost post = new HttpPost(url + "/login.jsp");

            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("os_username", username));
            urlParameters.add(new BasicNameValuePair("os_password", password));

            try {
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
            } catch (UnsupportedEncodingException e) {
                logger.warning("Content for request has unsupported encoding. Please, check your parameters");
                throw e;
            }

            RestClientResponse response;
            try {
                response = execute(post);
            } catch (IOException e) {
                logger.warning("POST request to get access_token failed to execute. Please, check your credentials/URL in jira.properties.");
                throw e;
            }
            StringBuilder cookies = new StringBuilder();
            for (Header h : response.getHeaders()) {
                if (h.getName().equals("Set-Cookie")) {
                    cookies.insert(0, h.getValue() + ";");
                }
            }
            authCookies = cookies.toString();
        }
        return authCookies;
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

    private URI getUri(String url) {
        if (!url.toLowerCase().startsWith("http")) {
            throw new IllegalArgumentException(String.format("URL must start with http prefix. Source url: '%s'", url));
        }
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            char illegalChar = url.toCharArray()[e.getIndex()];
            try {
                String encodedChar = URLEncoder.encode(String.valueOf(illegalChar), StandardCharsets.UTF_8.name());
                String update = url.replace(String.valueOf(illegalChar), encodedChar);
                return getUri(update);
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalArgumentException(String.format("Input string '%1$s' cannot be converted to URL without illegal characters", url));
            }
        }
    }

    @Override
    public List<RefStatus> getIssues(List<String> keys) throws IOException {
        String keysAsString = keys.stream().map(String::trim).collect(Collectors.joining(","));
        String searchUri = String.format("%1$s/rest/api/2/search?jql=issue in(%2$s)", getUrl(), keysAsString);
        HttpGet search = new HttpGet(getUri(searchUri));
        RestClientResponse response = send(search);
        SearchResponse searchResponse = objectMapper.readValue(response.getBody(), SearchResponse.class);
        return searchResponse.getIssuesAsInterface();
    }

    protected String listToQuotedStrings(List<String> list) {
        return list.stream().map(item -> "\"" + item + "\"")
                .collect(Collectors.joining(","));
    }
}