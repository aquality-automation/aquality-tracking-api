package main.utils;

import main.exceptions.AqualityException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtils {

    public List<String> doUpload(HttpServletRequest request, HttpServletResponse response, String destination) throws ServletException, IOException {
        List<String> files = new ArrayList<>();
        File destinationDir = new File(destination);
        if (!destinationDir.isDirectory() && !destinationDir.mkdirs() && !destinationDir.isDirectory()) {
            throw new IOException("Cannot create directory for uploaded files: " + destination);
        }

        Collection<Part> parts = request.getParts();
        for (Part filePart : parts) {
            String submittedName = filePart.getSubmittedFileName();
            if (submittedName == null || submittedName.trim().isEmpty()) {
                // Skip non-file multipart fields (suite, projectId, format, etc.)
                continue;
            }

            // Keep file name only: the client must not control directories
            String fileName = FilenameUtils.getName(submittedName);
            if (fileName.trim().isEmpty()) {
                continue;
            }

            String filePath = PathUtils.getUniquePath(PathUtils.createPath(destination, fileName));
            // I/O problems (no rights, no space, etc.) are not swallowed: import must not run with a part of the files
            try (InputStream fileContent = filePart.getInputStream();
                 OutputStream out = new FileOutputStream(new File(filePath))) {
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = fileContent.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                files.add(filePath);
            }
        }
        return files;
    }

    public void removeFiles(List<String> filePaths) {
        for (String path : filePaths) {
            removeFile(path);
        }
    }

    public void removeFile(String path) {
        new File(path).delete();
    }

    /**
     * Removes the directory with all its content (including partially uploaded files).
     * Never throws: missing directory is not an error.
     */
    public void removeDirectory(String path) {
        if (path != null) {
            org.apache.commons.io.FileUtils.deleteQuietly(new File(path));
        }
    }

    public String readFile(String path) throws AqualityException {
        try {
            InputStream is = new FileInputStream(path);
            return IOUtils.toString(is, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new AqualityException("File Not Found: " + e.getMessage());
        } catch (IOException e) {
            throw new AqualityException("Cannot read file: " + path);
        }
    }

    public String getFileFolderPath(String path) {
        return new File(path).getParent();
    }
}
