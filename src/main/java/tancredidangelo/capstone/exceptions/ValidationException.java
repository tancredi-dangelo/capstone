package tancredidangelo.capstone.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errorsList;

    public ValidationException(String message, List<String> errorsList) {
        super("Errors occurred during validation process.");
        this.errorsList = errorsList;
    }

    public List<String> getErrorsList() {
        return errorsList;
    }
}
