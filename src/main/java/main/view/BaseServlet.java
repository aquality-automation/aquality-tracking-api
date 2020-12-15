package main.view;

import main.Session;
import main.World;
import main.exceptions.AqualityException;
import main.exceptions.AqualityQueryParameterException;
import main.model.dto.DtoMapperGeneral;
import main.model.dto.ErrorDto;
import org.jetbrains.annotations.NotNull;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class BaseServlet extends HttpServlet {
    protected static Logger log = Logger.getLogger(BaseServlet.class.getName());
    protected DtoMapperGeneral mapper = new DtoMapperGeneral();
    protected static final String PROJECT_ID_KEY = "project_id";


    protected Session createSession(HttpServletRequest req) throws AqualityException, AuthenticationException {
        return new Session(getSessionId(req));
    }

    private String replacer(String value) {
        String data = value;
        try {
            data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            data = data.replaceAll("\\+", "%2B");
            data = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    protected String getRequestJson(@NotNull HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String s;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return replacer(sb.toString());
    }

    protected String getStringQueryParameter(@NotNull HttpServletRequest req, String name) {
        return (req.getParameterMap().containsKey(name) && !req.getParameter(name).equals(""))
                ? req.getParameter(name)
                : null;
    }

    protected Integer getIntegerQueryParameter(@NotNull HttpServletRequest req, String name) {
        return (req.getParameterMap().containsKey(name) && !req.getParameter(name).equals(""))
                ? Integer.parseInt(req.getParameter(name))
                : null;
    }

    protected Integer getProjectId(@NotNull HttpServletRequest req) {
        return getIntegerQueryParameter(req, PROJECT_ID_KEY);
    }

    protected Boolean getBooleanQueryParameter(@NotNull HttpServletRequest req, String name) {
        return (req.getParameterMap().containsKey(name) && !req.getParameter(name).equals("")) && Boolean.parseBoolean(req.getParameter(name));
    }

    protected void setPostResponseHeaders(@NotNull HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Methods", "POST");
        resp.addHeader("Access-Control-Allow-Origin", getOrigin());
        resp.addHeader("Access-Control-Allow-Headers", "Authorization");
    }

    protected void setEncoding(@NotNull HttpServletResponse resp) {
        resp.setCharacterEncoding(UTF_8.name());
    }

    protected void setJSONContentType(@NotNull HttpServletResponse resp) {
        resp.setContentType(APPLICATION_JSON);
    }

    protected void setDeleteResponseHeaders(@NotNull HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Methods", "DELETE, POST");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Origin", getOrigin());
        resp.addHeader("Access-Control-Allow-Headers", "Authorization");
    }

    protected void setGetResponseHeaders(@NotNull HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Methods", "GET");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Origin", getOrigin());
        resp.addHeader("Access-Control-Allow-Headers", "Authorization");
    }

    protected void setOptionsResponseHeaders(@NotNull HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", "OPTIONS, DELETE, POST, GET, PUT");
        resp.addHeader("Access-Control-Allow-Origin", getOrigin());
        resp.addHeader("Access-Control-Allow-Headers", "Authorization, content-type");
        resp.setStatus(204);
    }

    private String getOrigin() {
        return World.getInstance().getBaseURL() != null ? World.getInstance().getBaseURL() : "*";
    }

    private void setAuthorizationProblem(@NotNull HttpServletResponse resp, @NotNull Exception e) throws AqualityException {
        resp.setStatus(401);
        setResponseBody(resp, !Objects.equals(e.getMessage(), "") ? e.getMessage() : "Are you sure you logged in?");
    }

    protected void setAuthorizationProblem(@NotNull HttpServletResponse resp) throws AqualityException {
        setAuthorizationProblem(resp, new Exception("Are you sure you logged in?"));
    }

    private String getSessionId(@NotNull HttpServletRequest req) throws AqualityException, AuthenticationException {
        String header = req.getHeader("Authorization");
        Cookie[] cookies = req.getCookies();
        if (header != null) {
            validateAuthHeader(header);
            String[] strings = header.split(" ");
            return strings[1];
        } else if (cookies != null) {
            Cookie iio78 = Arrays.stream(cookies).filter(x -> x.getName().equals("iio78")).findFirst().orElse(null);
            if (iio78 != null) {
                try {
                    return URLDecoder.decode(iio78.getValue(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new AuthenticationException("Your cookie is wrong!");
                }
            }
        }
        throw new AuthenticationException("You've missed your authorization header!");
    }

    private void validateAuthHeader(String header) throws AqualityException {
        if (!header.toLowerCase().startsWith("basic ".toLowerCase())) {
            throw new AqualityException("Use Basic Authorization Header! (Should start with 'Basic ')");
        }
    }

    protected void processResponse(HttpServletResponse response, String filePath) {
        File downloadFile = new File(filePath);
        try (OutputStream outStream = response.getOutputStream(); FileInputStream inStream = new FileInputStream(downloadFile)) {

            ServletContext context = getServletContext();

            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            handleException(response, e);
        }
    }

    protected void setResponseBody(HttpServletResponse resp, Object object) throws AqualityException {
        try {
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(object));
        } catch (IOException e) {
            throw new AqualityException("System was not able to write a response! Raise an Issue.");
        }
    }

    protected void handleException(HttpServletResponse resp, @NotNull Exception e) {
        e.printStackTrace();
        try {
            switch (e.getClass().getSimpleName()) {
                case "UnsupportedOperationException":
                    setNotImplementedFunction(resp, e);
                    return;
                case "AuthenticationException":
                    setAuthorizationProblem(resp, e);
                    return;
                case "AqualityParametersException":
                case "AqualityPermissionsException":
                case "AqualityException":
                case "InvalidFormatException":
                case "AqualityQueryParameterException":
                case "AqualitySQLException":
                    AqualityException exception = (AqualityException) e;
                    resp.setStatus(exception.getResponseCode());
                    setResponseBody(resp, new ErrorDto(exception.getMessage()));
                    return;
                default:
                    setUnknownIssue(resp);
            }
        } catch (AqualityException ex) {
            handleException(resp, ex);
        }
    }

    private void setNotImplementedFunction(@NotNull HttpServletResponse resp, @NotNull Exception e) throws AqualityException {
        resp.setStatus(501);
        setResponseBody(resp, new ErrorDto(e.getMessage()));
    }

    private void setUnknownIssue(@NotNull HttpServletResponse resp) throws AqualityException {
        resp.setStatus(500);
        setResponseBody(resp, new ErrorDto("Unknown Issue."));
    }

    protected void assertRequiredField(HttpServletRequest request, String fieldName) throws AqualityException {
        String fieldValue = getStringQueryParameter(request, fieldName);
        if (fieldValue == null) {
            throw new AqualityQueryParameterException(fieldName);
        }
    }
}
