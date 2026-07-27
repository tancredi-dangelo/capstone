package tancredidangelo.capstone.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tancredidangelo.capstone.exceptions.BadRequestException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;
import tancredidangelo.capstone.exceptions.ValidationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    // 1. Validation Error -> 400 BadRequest
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationExceptions(ValidationException ex) {
        ex.printStackTrace();
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrorsList());
    }

    // 2. Generic BadRequest
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(BadRequestException ex) {
        ex.printStackTrace();
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 3. Unauthorized Exception -> 401 Unautorized
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorized(UnauthorizedException ex) {
        ex.printStackTrace();
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 4. Not Found Exception -> 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFound(NotFoundException ex) {
        ex.printStackTrace();
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 5. Access Denied -> 403 Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAccessDenied(AccessDeniedException ex) {
        ex.printStackTrace();
        return new ErrorDTO("Authorization Failed.", LocalDateTime.now());
    }

    // 6. 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleGenericError(Exception ex) {
        ex.printStackTrace();
        return new ErrorDTO("Errore interno del server: " + ex.getMessage(), LocalDateTime.now());
    }
}
