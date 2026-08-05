package tancredidangelo.capstone.exceptions;

public class BannedAccountException extends RuntimeException {
    public BannedAccountException(String message) {
        super(message);
    }
}
