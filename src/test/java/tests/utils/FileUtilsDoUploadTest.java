package tests.utils;

import main.utils.FileUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.StubPart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FileUtilsDoUploadTest {

    private File tempDir;
    private FileUtils fileUtils;

    @BeforeMethod
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("file-utils-upload-").toFile();
        fileUtils = new FileUtils();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (tempDir != null && tempDir.exists()) {
            Files.walk(tempDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void doUpload_shouldIgnoreMultipartFieldsWithoutFilename() throws Exception {
        HttpServletRequest request = requestWithParts(
                StubPart.file("file", "suite1.trx", "<TestRun/>".getBytes(StandardCharsets.UTF_8)),
                StubPart.text("projectId", "1".getBytes(StandardCharsets.UTF_8)),
                StubPart.text("suite", "Suite-1".getBytes(StandardCharsets.UTF_8)),
                StubPart.text("format", "MSTest".getBytes(StandardCharsets.UTF_8))
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<String> uploaded = fileUtils.doUpload(request, response, tempDir.getAbsolutePath());

        assertEquals(uploaded.size(), 1, "Only the real file part should be saved");
        assertTrue(uploaded.get(0).endsWith("suite1.trx"));
        assertTrue(new File(uploaded.get(0)).isFile());
        assertFalse(new File(tempDir, "null").exists(), "Text fields must not be saved as file named null");
        assertEquals(Files.readAllBytes(new File(uploaded.get(0)).toPath()), "<TestRun/>".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void doUpload_shouldNotCloseResponseWriter() throws Exception {
        HttpServletRequest request = requestWithParts(
                StubPart.file("file", "report.trx", "data".getBytes(StandardCharsets.UTF_8))
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        fileUtils.doUpload(request, response, tempDir.getAbsolutePath());

        response.getWriter().write("{\"ok\":true}");
        response.getWriter().flush();
        assertEquals(response.getContentAsString(), "{\"ok\":true}");
    }

    @Test
    public void doUpload_shouldUseOnlyFileNameWithoutPath() throws Exception {
        HttpServletRequest request = requestWithParts(
                StubPart.file("file", "../../evil.trx", "x".getBytes(StandardCharsets.UTF_8))
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<String> uploaded = fileUtils.doUpload(request, response, tempDir.getAbsolutePath());

        assertEquals(uploaded.size(), 1);
        File saved = new File(uploaded.get(0));
        assertEquals(saved.getName(), "evil.trx");
        assertEquals(saved.getParentFile().getCanonicalPath(), tempDir.getCanonicalPath());
    }

    @Test
    public void doUpload_shouldUploadMultipleFilesAndSkipEmptyFilename() throws Exception {
        HttpServletRequest request = requestWithParts(
                StubPart.file("file1", "a.trx", "A".getBytes(StandardCharsets.UTF_8)),
                StubPart.file("file2", "b.trx", "B".getBytes(StandardCharsets.UTF_8)),
                StubPart.file("emptyName", "", "should-skip".getBytes(StandardCharsets.UTF_8))
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<String> uploaded = fileUtils.doUpload(request, response, tempDir.getAbsolutePath());

        assertEquals(uploaded.size(), 2);
        assertTrue(uploaded.stream().anyMatch(path -> path.endsWith("a.trx")));
        assertTrue(uploaded.stream().anyMatch(path -> path.endsWith("b.trx")));
    }

    private HttpServletRequest requestWithParts(Part... parts) {
        List<Part> partList = new ArrayList<>();
        Collections.addAll(partList, parts);
        return new HttpServletRequestWrapper(new org.springframework.mock.web.MockHttpServletRequest()) {
            @Override
            public Collection<Part> getParts() {
                return partList;
            }
        };
    }
}
