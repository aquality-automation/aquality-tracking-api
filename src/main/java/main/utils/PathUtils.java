package main.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathUtils {
    public static String createPath(String... parts){
        return String.join(File.separator, Arrays.asList(parts));
    }

    public static String createPathToBin(String... parts){
        List<String> strings = new ArrayList<>(Arrays.asList(parts));
        strings.add(0, System.getProperty("user.dir"));
        return createPath(strings.toArray(new String[0]));
    }

    public static String getUniquePath(String filePath) {
        File file = new File(filePath);
        int fileIndex = 1;
        String extension = FilenameUtils.getExtension(filePath);
        String newPath = filePath;
        while (file.exists()) {
            newPath = FilenameUtils.removeExtension(filePath).concat(String.format(" (%s)", fileIndex));
            if (!extension.equals("")) {
                newPath = newPath.concat(FilenameUtils.EXTENSION_SEPARATOR_STR).concat(extension);
            }

            file = new File(newPath);
            fileIndex++;
        }

        return newPath;
    }
}
