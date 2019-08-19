package main.exceptions;

public class RPQueryParameterException extends RPException {
    public RPQueryParameterException(String paramName) {
        super(String.format("Request Parameter '%s' is required ", paramName));
        this.responseCode = 422;
    }
}
