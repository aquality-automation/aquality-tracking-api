package main.exceptions;

import java.sql.SQLException;

public class AqualitySQLException extends AqualityException {

    public AqualitySQLException(SQLException exception) {
        super(getErrorMessage(exception));
        this.responseCode = getErrorCode(exception.getSQLState());
    }


    private static String getErrorMessage(SQLException exception){
        switch (exception.getSQLState()){
            case "23516":
            case "45000":
            case "23505":
                return "You are trying to create duplicate entity.";
            case "42000":
                return "You Regular expression is not valid!";
            case "40001":
                return "You are trying to edit entity which is locked. Please retry the operation.";
            case "23000":
                return String.format("You have missed required parameter: %s", exception.getMessage());
            default:
                return String.format("Unknown SQL Error: %s", exception.getSQLState());
        }
    }

    private static Integer getErrorCode(String sqlcode){
        switch (sqlcode){
            case "23516":
            case "45000":
            case "23505":
                return 409;
            case "42000":
            case "23000":
                return 400;
            default:
                return 500;
        }
    }
}
