package tancredidangelo.heliosspaces.exceptions;

public class BannedAccountException extends RuntimeException {
    public BannedAccountException(String message) {
        super(message);
    }
}
