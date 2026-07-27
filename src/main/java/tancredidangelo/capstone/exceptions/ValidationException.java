package tancredidangelo.capstone.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errorsList;


    public ValidationException(String message) {
        super(message);
        this.errorsList = new ArrayList<>();
    }

    // optional constructor for error List
    public ValidationException(String message, List<String> errorsList) {
        super(message);
        this.errorsList = errorsList;
    }

    // getter error List
    public List<String> getErrorsList() {
        return errorsList;
    }
}
