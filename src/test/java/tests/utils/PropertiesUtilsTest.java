package tests.utils;

import main.utils.AppProperties;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class PropertiesUtilsTest {
    @Test
    public void getAppNameTest(){
        AppProperties appProperties = new AppProperties();
        assertEquals("Aquality Tracking", appProperties.getName());
    }
}
