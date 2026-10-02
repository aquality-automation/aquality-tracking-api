package utils;

import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

/**
 * Lightweight {@link Part} stub for unit tests without a real multipart container.
 */
public class StubPart implements Part {
    private final String name;
    private final String filename;
    private final byte[] content;

    public StubPart(String name, String filename, byte[] content) {
        this.name = name;
        this.filename = filename;
        this.content = content;
    }

    public static StubPart file(String name, String filename, byte[] content) {
        return new StubPart(name, filename, content);
    }

    public static StubPart text(String name, byte[] content) {
        return new StubPart(name, null, content);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getContentType() {
        return filename == null ? "text/plain" : "application/xml";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSubmittedFileName() {
        return filename;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public void write(String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() {
    }

    @Override
    public String getHeader(String name) {
        if ("content-disposition".equalsIgnoreCase(name)) {
            if (filename == null) {
                return "form-data; name=\"" + this.name + "\"";
            }
            return "form-data; name=\"" + this.name + "\"; filename=\"" + filename + "\"";
        }
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        String value = getHeader(name);
        return value == null ? Collections.emptyList() : Collections.singletonList(value);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Collections.singletonList("content-disposition");
    }
}
