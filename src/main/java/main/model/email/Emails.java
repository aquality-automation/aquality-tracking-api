package main.model.email;

import main.exceptions.AqualityException;
import main.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

abstract class Emails {
    DateUtils dateUtils = new DateUtils();

    String hostUri() throws AqualityException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();

        InputStream resource = classloader.getResourceAsStream("hostinfo.properties");
        try {
            prop.load(resource);
        } catch (IOException e) {
            throw new AqualityException("Not able to load properties");
        }

        return String.format("http://%s/", prop.getProperty("frontURI"));
    }
}
