package tests.workers.view;

import main.exceptions.AqualityParametersException;
import main.model.db.imports.ImportTypes;
import main.view.Project.ExecuteImportServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ExecuteImportServletTest {

    private static final int CONCURRENT_THREADS = 20;
    private static final int PROJECT_ID_BASE = 1000;
    private static final long WORKERS_READY_TIMEOUT_SECONDS = 5;
    private static final long TASK_TIMEOUT_SECONDS = 10;

    private static final int FIRST_PROJECT_ID = 101;
    private static final int SECOND_PROJECT_ID = 202;

    @Test
    public void readParameters_shouldKeepRequestDataIsolatedBetweenSequentialCalls() throws Exception {
        ExecuteImportServlet servlet = new ExecuteImportServlet();

        Object requestData1 = invokeReadParameters(servlet, buildRequest(FIRST_PROJECT_ID, "Suite 1", "Build-1", "alice"));
        Object requestData2 = invokeReadParameters(servlet, buildRequest(SECOND_PROJECT_ID, "Suite 2", "Build-2", "bob"));

        assertNotSame(requestData1, requestData2);
        assertEquals(getFieldValue(requestData1, "projectId"), FIRST_PROJECT_ID);
        assertEquals(getFieldValue(requestData1, "suiteName"), "Suite 1");
        assertEquals(getFieldValue(requestData1, "buildName"), "Build-1");
        assertEquals(getFieldValue(requestData1, "author"), "alice");

        assertEquals(getFieldValue(requestData2, "projectId"), SECOND_PROJECT_ID);
        assertEquals(getFieldValue(requestData2, "suiteName"), "Suite 2");
        assertEquals(getFieldValue(requestData2, "buildName"), "Build-2");
        assertEquals(getFieldValue(requestData2, "author"), "bob");
    }

    @Test
    public void readParameters_shouldKeepRequestDataIsolatedUnderConcurrentCalls() throws Exception {
        ExecuteImportServlet servlet = new ExecuteImportServlet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch ready = new CountDownLatch(CONCURRENT_THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        List<Future<?>> futures = new ArrayList<>();
        List<AtomicReference<Object>> results = new ArrayList<>();

        try {
            for (int i = 0; i < CONCURRENT_THREADS; i++) {
                final int index = i;
                AtomicReference<Object> result = new AtomicReference<>();
                results.add(result);
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    MockHttpServletRequest request = buildRequest(
                            PROJECT_ID_BASE + index,
                            "Suite-" + index,
                            "Build-" + index,
                            "user-" + index
                    );
                    result.set(invokeReadParameters(servlet, request));
                    return null;
                }));
            }

            assertTrue(ready.await(WORKERS_READY_TIMEOUT_SECONDS, TimeUnit.SECONDS), "Workers did not become ready in time");
            start.countDown();

            for (Future<?> future : futures) {
                future.get(TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }

            for (int i = 0; i < CONCURRENT_THREADS; i++) {
                Object params = results.get(i).get();
                assertNotNull(params, "Params were not captured for thread " + i);
                assertEquals(getFieldValue(params, "projectId"), PROJECT_ID_BASE + i);
                assertEquals(getFieldValue(params, "suiteName"), "Suite-" + i);
                assertEquals(getFieldValue(params, "buildName"), "Build-" + i);
                assertEquals(getFieldValue(params, "author"), "user-" + i);
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    public void readParameters_shouldRejectUnsupportedFormat() throws Exception {
        ExecuteImportServlet servlet = new ExecuteImportServlet();
        MockHttpServletRequest request = buildRequest(FIRST_PROJECT_ID, "Suite 1", "Build-1", "alice");
        request.setParameter("format", "NotExistingFormat");

        try {
            invokeReadParameters(servlet, request);
            fail("Unsupported format must be rejected before the import is accepted");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof AqualityParametersException, "Unexpected exception: " + e.getCause());
            assertEquals(((AqualityParametersException) e.getCause()).getResponseCode(), Integer.valueOf(400));
        }
    }

    private MockHttpServletRequest buildRequest(int projectId, String suite, String buildName, String author) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("projectId", String.valueOf(projectId));
        request.setParameter("format", ImportTypes.MSTest.name());
        request.setParameter("suite", suite);
        request.setParameter("buildName", buildName);
        request.setParameter("author", author);
        request.setParameter("singleTestRun", "true");
        request.setParameter("debug", "false");
        return request;
    }

    private Object invokeReadParameters(ExecuteImportServlet servlet, HttpServletRequest request) throws Exception {
        Method method = ExecuteImportServlet.class.getDeclaredMethod("readParameters", HttpServletRequest.class);
        method.setAccessible(true);
        return method.invoke(servlet, request);
    }

    private Object getFieldValue(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
