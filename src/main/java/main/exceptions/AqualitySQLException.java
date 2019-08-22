package main.exceptions;

public class AqualitySQLException extends AqualityException {

    public AqualitySQLException(String sqlcode) {
        super(getErrorMessage(sqlcode));
        this.responseCode = getErrorCode(sqlcode);
    }



    private static String getErrorMessage(String sqlcode){
        switch (sqlcode){
            case "23516":
            case "45000":
            case "23505":
                return "You are trying to create duplicate entity.";
            default:
                return String.format("Unknown SQL Error: %s", sqlcode);
        }
    }

    private static Integer getErrorCode(String sqlcode){
        switch (sqlcode){
            case "23516":
            case "45000":
            case "23505":
                return 409;
            default:
                return 500;
        }
    }
}
