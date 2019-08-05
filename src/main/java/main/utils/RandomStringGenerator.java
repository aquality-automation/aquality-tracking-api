package main.utils;

import java.util.UUID;

public class RandomStringGenerator {

    public static String generateString() {
        return UUID.randomUUID().toString();
    }
}
