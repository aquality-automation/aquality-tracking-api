package main.utils;

import main.exceptions.RPException;
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
        new File(destination).mkdirs();
        Collection<Part> parts = request.getParts();
        for (Part filePart:parts) {
            System.out.printf("part Type: %s",filePart.getContentType());
            OutputStream out = null;
            InputStream fileContent = null;
            PrintWriter writer = response.getWriter();
            String fileName = getFileName(filePart);

            try {
                String filePath = PathUtils.createPath(destination, fileName);
                out = new FileOutputStream(new File(filePath));
                fileContent = filePart.getInputStream();
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = fileContent.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                files.add(filePath);
            } catch (FileNotFoundException fne) {
                System.out.println("You either did not specify a file to upload or are trying to upload a file to a protected or nonexistent location.");
                System.out.println("<br/> ERROR: " + fne.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (fileContent != null) {
                    fileContent.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
        }
        return files;
    }

    public void removeFiles(List<String> filePaths){
        for (String path : filePaths){
            boolean delete = new File(path).delete();
        }
    }

    public void removeFile(String path){
        boolean delete = new File(path).delete();
    }

    public String readFile(String path) throws RPException {
        try {
            InputStream is = new FileInputStream(path);
            return IOUtils.toString(is, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new RPException("File Not Found: " + e.getMessage());
        } catch (IOException e) {
            throw new RPException("Cannot read file: " + path);
        }
    }

    public String readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        return IOUtils.toString(is, "UTF-8");
    }

    private String getFileName(final Part part) {
        part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
