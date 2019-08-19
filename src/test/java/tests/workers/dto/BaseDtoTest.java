package tests.workers.dto;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.exceptions.AqualityException;
import main.model.dto.TestResultDto;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;
import testData.TestDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class BaseDtoTest {
    private TestDto testDto;
    public Date date = new Date();
    public BaseDtoTest(){
        testDto = new TestDto();
        testDto.setDateValue(date);
        testDto.setId(10);
        testDto.setNoAnnotation("noAnnotation");
        testDto.setStringValue("string");
        testDto.setIntegerValue(1010);
    }

    @Test
    public void getParametersTest(){
        try {
            List<Pair<String, String>> actual = testDto.getParameters();
            List<Pair<String, String>> expected = new ArrayList<>();
            expected.add(new Pair<>("request_id", "10"));
            expected.add(new Pair<>("request_integer", "1010"));
            expected.add(new Pair<>("request_date", Long.toString(date.getTime() / 1000)));

            assertPairs(actual, expected);
        } catch (AqualityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSearchParametersTest(){
        try {
            List<Pair<String, String>> actual = testDto.getSearchParameters();
            List<Pair<String, String>> expected = new ArrayList<>();
            expected.add(new Pair<>("request_id", "10"));
            expected.add(new Pair<>("request_integer", "1010"));

            assertPairs(actual, expected);
        } catch (AqualityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getIdParametersTest(){
        try {
            List<Pair<String, String>> actual = testDto.getIDParameters();
            List<Pair<String, String>> expected = new ArrayList<>();
            expected.add(new Pair<>("request_id", "10"));

            assertPairs(actual, expected);
        }catch (AqualityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fillFromRequestParameters(){
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServerName("www.example.com");
            request.setRequestURI("/foo");
            request.setParameter("notSearch", "value1");
            request.setParameter("project_id", "123");
            request.setParameter("id", "1234");
            request.setParameter("test_id", "111");
            TestResultDto result = new TestResultDto();
            result.getSearchTemplateFromRequestParameters(request);
            assertEquals((int)result.getId(), 1234);
            assertEquals((int)result.getProject_id(), 123);
            assertEquals((int)result.getTest_id(), 111);
        } catch (AqualityException e) {
            e.printStackTrace();
        }
    }

    private void assertPairs(List<Pair<String, String>> actual, List<Pair<String, String>> expected){
        assertEquals(actual.size(), expected.size());

        for (Pair<String, String> actualPair: actual) {
            Pair<String, String> expectedPair  = expected.stream().filter(pair -> pair.left.equals(actualPair.left)).findFirst().orElse(null);
            assertNotNull(expectedPair);
            assertEquals(actualPair.right, expectedPair.right);
        }
    }
}
