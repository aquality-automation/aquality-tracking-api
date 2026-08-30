package main.utils;

import main.exceptions.AqualityException;
import org.apache.commons.io.IOUtils;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtils {
    private static final Logger log = Logger.getLogger(FileUtils.class.getName());
    private static final String HEADER_CONTENT_DISPOSITION = "content-disposition";

    public List<String> doUpload(HttpServletRequest request, HttpServletResponse response, String destination) throws ServletException, IOException {
        List<String> files = new ArrayList<>();
        new File(destination).mkdirs();
        Collection<Part> parts = request.getParts();
        for (Part filePart : parts) {
            OutputStream out = null;
            InputStream fileContent = null;
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.trim().isEmpty()) {
                log.info("Skipping non-file part: " + filePart.getName() + " content-type: " + filePart.getContentType());
                continue;
            }

            long partSize = -1;
            try {
                partSize = filePart.getSize();
            } catch (Throwable ignored) {
                // Some servlet containers may not support getSize(); ignore and proceed
            }
            if (partSize == 0) {
                log.info("Skipping zero-length file part: " + fileName + " size=" + partSize);
                continue;
            }

            try {
                String uniqueFileName = java.util.UUID.randomUUID() + "_" + fileName;
                String filePath = PathUtils.createPath(destination, uniqueFileName);
                out = new FileOutputStream(new File(filePath));
                fileContent = filePart.getInputStream();
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = fileContent.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                files.add(filePath);
            } catch (FileNotFoundException fne) {
                log.warning("You either did not specify a file to upload or are trying to upload a file to a protected or nonexistent location.");
                log.warning("<br/> ERROR: " + fne.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (fileContent != null) {
                    fileContent.close();
                }
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

    private String getFileName(final Part part) {
        part.getHeader(HEADER_CONTENT_DISPOSITION);
        for (String content : part.getHeader(HEADER_CONTENT_DISPOSITION).split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
