package main.exceptions;

public class AqualityParametersException extends AqualityException {

    public AqualityParametersException(String error) {
        super(error);
        this.responseCode = 400;
    }

    public AqualityParametersException(String error, Object... args) {
        super(error, args);
        this.responseCode = 400;
    }
}
