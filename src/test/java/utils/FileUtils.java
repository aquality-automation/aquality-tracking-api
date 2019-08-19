package utils;

import main.exceptions.AqualityException;
import org.apache.poi.openxml4j.opc.internal.FileHelper;

import java.io.File;

public class FileUtils {

    public static String getResourceFileAsString(String fileName) {
        File file = getResourceFile(fileName);
        try {
            String fp = file.getPath();
            fp = fp.replaceAll("%20", " ");
            main.utils.FileUtils fu = new main.utils.FileUtils();
            return fu.readFile(fp);
        } catch (AqualityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getResourceFile(String fileName){
        ClassLoader classLoader = FileHelper.class.getClassLoader();
        return  new File(classLoader.getResource(fileName).getFile());
    }
}
