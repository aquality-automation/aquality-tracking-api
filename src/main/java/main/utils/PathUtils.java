package main.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathUtils {
    public static String createPath(String[] parts){
        return String.join(File.separator, Arrays.asList(parts));
    }

    public static String createPathToBin(String[] parts){
        List<String> strings = new ArrayList<>(Arrays.asList(parts));
        strings.add(0, System.getProperty("user.dir"));
        return createPath(strings.toArray(new String[0]));
    }
}
