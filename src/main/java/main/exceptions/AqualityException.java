package main.exceptions;

import lombok.Getter;

public class AqualityException extends Exception {
    @Getter
    protected Integer responseCode = 500;
    public AqualityException(String error) {
        super(error);
    }
}
