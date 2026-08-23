package tests.workers.view;

import main.model.db.imports.ImportTypes;
import main.view.Project.ExecuteImportServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

public class ExecuteImportServletTest {

    @Test
    public void readParameters_shouldKeepRequestDataIsolatedBetweenCalls() throws Exception {
        ExecuteImportServlet servlet = new ExecuteImportServlet();

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.setParameter("projectId", "101");
        request1.setParameter("format", ImportTypes.MSTest.name());
        request1.setParameter("suite", "Suite 1");
        request1.setParameter("buildName", "Build-1");
        request1.setParameter("author", "alice");
        request1.setParameter("singleTestRun", "true");
        request1.setParameter("debug", "false");

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.setParameter("projectId", "202");
        request2.setParameter("format", ImportTypes.MSTest.name());
        request2.setParameter("suite", "Suite 2");
        request2.setParameter("buildName", "Build-2");
        request2.setParameter("author", "bob");
        request2.setParameter("singleTestRun", "true");
        request2.setParameter("debug", "false");

        Object requestData1 = invokeReadParameters(servlet, request1);
        Object requestData2 = invokeReadParameters(servlet, request2);

        assertNotSame(requestData1, requestData2);
        assertEquals(getFieldValue(requestData1, "projectId"), 101);
        assertEquals(getFieldValue(requestData1, "suiteName"), "Suite 1");
        assertEquals(getFieldValue(requestData1, "buildName"), "Build-1");
        assertEquals(getFieldValue(requestData2, "projectId"), 202);
        assertEquals(getFieldValue(requestData2, "suiteName"), "Suite 2");
        assertEquals(getFieldValue(requestData2, "buildName"), "Build-2");
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
