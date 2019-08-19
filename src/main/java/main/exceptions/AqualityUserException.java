package main.exceptions;

public class AqualityUserException extends AqualityException {
    public AqualityUserException(String error) {
        super(String.format("[USER]: %s", error));
    }
}
