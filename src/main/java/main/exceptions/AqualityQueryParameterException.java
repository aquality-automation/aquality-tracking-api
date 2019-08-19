package main.exceptions;

public class AqualityQueryParameterException extends AqualityException {
    public AqualityQueryParameterException(String paramName) {
        super(String.format("Request Parameter '%s' is required ", paramName));
    }
}
