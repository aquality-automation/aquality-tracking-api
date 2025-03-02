package main.exceptions;

import java.sql.SQLException;

public class AqualitySQLException extends AqualityException {

    public AqualitySQLException(SQLException exception) {
        super(getErrorMessage(exception));
        this.responseCode = getErrorCode(exception.getSQLState());
    }

    private static String getErrorMessage(SQLException exception){
        switch (exception.getSQLState()){
            case "45000":
                return exception.getMessage();
            case "23516":
            case "23505":
                return "You are trying to create duplicate entity.";
            case "42000":
                return "You Regular expression is not valid!";
            case "40001":
                return "You are trying to edit entity which is locked. Please retry the operation.";
            case "23000":
                if(exception.getMessage().contains("Cannot delete or update a parent row")){
                    return "You are trying to remove entity that is used in other place!";
                }
                return String.format("You have missed required parameter: %s", exception.getMessage());
            case "HY000":
                return String.format("Data base error: %s", exception.getMessage());
            case "42S02":
                return "There is some missed table in your Data Base, please contact administrator.";
            case "S1000":
                return "The Data Size is too big. Possibly you are trying to attach file > 3 mb.";
            default:
                return String.format("Unknown SQL Error: %s \n Message: %s", exception.getSQLState(), exception.getMessage());
        }
    }

    private static Integer getErrorCode(String sqlcode){
        switch (sqlcode){
            case "23516":
            case "45000":
            case "23505":
                return 409;
            case "S1000":
            case "42000":
            case "23000":
            case "HY000":
            case "42S02":
                return 400;
            default:
                return 500;
        }
    }
}
