package main.exceptions;

public class RPUserException extends RPException {
    public RPUserException(String error) {
        super(String.format("[USER]: %s", error));
        this.responseCode = 403;
    }
}
