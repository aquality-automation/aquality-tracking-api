package main.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtil {

    public static boolean compareByRegexpGroups(String actual, String expected, String expression){
        Pattern pattern = Pattern.compile(expression, Pattern.DOTALL);
        List<String> actualGroups = getGroups(pattern.matcher(actual));
        List<String> expectedGroups = getGroups(pattern.matcher(expected));

        boolean result = actualGroups.size() == expectedGroups.size() && actualGroups.size() > 0;

        if(result) {
            for (int i = 0; i < actualGroups.size(); i++) {
                result = actualGroups.get(i).equals(expectedGroups.get(i));
                if(!result) {
                    break;
                }
            }
        }

        return result;
    }

    public static boolean match(String value, String expression) {
        Pattern pattern = Pattern.compile(expression, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private static List<String> getGroups(Matcher matcher) {
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
        }
        return groups;
    }
}
