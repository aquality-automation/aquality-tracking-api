package main.utils.integrations.atlassian;

import org.apache.http.Header;

import java.util.List;
import java.util.stream.Collectors;

public class RestClientResponse {

    private String body;
    private Header[] headers;
    private int statusCode;
    private String requestUri;

    // we need this constructor just to support serializing and de-serializing to/from xml
    public RestClientResponse() {
        this.body = null;
        this.requestUri = "UNDEFINED";
        this.statusCode = 999;
        this.headers = new Header[]{};
    }

    public RestClientResponse(String requestUri, String body, int statusCode, Header[] headers) {
        this.body = body;
        this.requestUri = requestUri;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public boolean isBodyEmpty() {
        return body == null || body.isEmpty();
    }

    public Header[] getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Response{" +
                "url='" + requestUri + '\'' +
                ", responseCode=" + statusCode +
                ", isBodyEmpty=" + isBodyEmpty() +
                '}';
    }

    public static String getMessage(List<RestClientResponse> responses) {
        return responses.stream().map(RestClientResponse::toString).collect(Collectors.joining("\\n"));
    }
}
