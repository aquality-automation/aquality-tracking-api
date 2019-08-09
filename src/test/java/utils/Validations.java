package utils;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class Validations {
    public static void assertSQLToParams(String sql, List<Pair<String, String>> parameters){
        long expectedNumberOfParameters = sql != null
                ? sql.chars().filter(ch -> ch == '?').count()
                : 0;
        assertEquals(parameters.size(), new Long(expectedNumberOfParameters).intValue());
    }

    public static void assertSQLToParams(String sql, int expectedNumber){
        long expectedNumberOfParameters = sql != null
                ? sql.chars().filter(ch -> ch == '?').count()
                : 0;
        assertEquals(expectedNumber, new Long(expectedNumberOfParameters).intValue());
    }
}
