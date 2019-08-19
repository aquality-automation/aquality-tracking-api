package main.exceptions;

import lombok.Getter;

public class RPException extends Exception {

    @Getter
    protected Integer responseCode = 500;
    public RPException(String error) {
        super(error);
    }
}
