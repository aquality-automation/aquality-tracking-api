package main.utils;

import main.exceptions.RPException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertyUtils {
    Properties prop;

    PropertyUtils(AvailableProperties property) {
        try (InputStream input = PropertyUtils.class.getClassLoader().getResourceAsStream(property.getName())) {

            prop = new Properties();

            if (input == null) {
                throw new RPException(String.format("Sorry, unable to find %s.", property.getName()));
            }
            prop.load(input);
        }catch (RPException | IOException e) {
            e.printStackTrace();
        }
    }

}
