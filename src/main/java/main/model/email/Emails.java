package main.model.email;

import main.exceptions.RPException;
import main.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

abstract class Emails {
    DateUtils dateUtils = new DateUtils();

    String hostUri() throws RPException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();

        InputStream resource = classloader.getResourceAsStream("hostinfo.properties");
        try {
            prop.load(resource);
        } catch (IOException e) {
            throw new RPException("Not able to load properties");
        }

        return String.format("http://%s/", prop.getProperty("frontURI"));
    }
}
