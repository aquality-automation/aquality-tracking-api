package tests.utils;

import main.utils.PathUtils;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.AssertJUnit.assertEquals;

public class PathUtilsTest {
    @Test
    public void createPathTest(){
        assertEquals("root" + File.separator + "folder1" + File.separator + "folder2", PathUtils.createPath("root", "folder1", "folder2"));
    }

    @Test
    public void createPathToBinTest(){
        assertEquals(System.getProperty("user.dir") + File.separator + "folder1" + File.separator + "folder2", PathUtils.createPathToBin("folder1", "folder2"));
    }
}
