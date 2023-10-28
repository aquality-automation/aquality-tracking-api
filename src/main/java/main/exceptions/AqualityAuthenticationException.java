package main.exceptions;

public class AqualityAuthenticationException extends  AqualityException {
    public AqualityAuthenticationException(String error)  {
        super(error);
        this.responseCode = 401;
    }
}