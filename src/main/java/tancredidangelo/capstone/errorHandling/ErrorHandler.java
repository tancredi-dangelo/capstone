package tancredidangelo.capstone.errorHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tancredidangelo.capstone.exceptions.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // 0. Native Spring @Valid Error -> 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<String> errorsList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        log.warn("Spring Validation Failed: {}", errorsList);

        return new ErrorDTO("Validation failed for input fields", LocalDateTime.now(), errorsList);
    }

    // 1. Validation Error -> 400 Bad Request
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationExceptions(ValidationException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrorsList());
    }

    // 2. Generic BadRequest
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequest(BadRequestException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 3. Unauthorized Exception -> 401 Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorized(UnauthorizedException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 4. Not Found Exception -> 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFound(NotFoundException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 5. Access Denied (Spring Legacy version) -> 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access Denied: You don't have permissions to access this resource.");
        return new ErrorDTO("Authorization Failed.", LocalDateTime.now());
    }

    // 5. Access Denied (Spring Security 6.3+) -> 403 Forbidden
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Access Denied: You don't have permissions to access this resource.");
        return new ErrorDTO("Authorization Failed.", LocalDateTime.now());
    }

    // 6. Message Not Readable (Malformed JSON) -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ErrorDTO("Malformed JSON request or invalid data types provided.", LocalDateTime.now());
    }

    // 7. Already Exists -> 409 Conflict
    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleAlreadyExists(AlreadyExistsException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    // 8. 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleGenericError(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return new ErrorDTO("Internal Server Error: Something went wrong, try again later.", LocalDateTime.now());
    }

    // 9. No Resource Found Exception
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNoResourceFound(NoResourceFoundException ex) {
        ex.printStackTrace();
        return new ErrorDTO("Resource Not Found.", LocalDateTime.now());
    }

    // 10. Http Request Method Not Supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ex.printStackTrace();
        return new ErrorDTO("Method Not Allowed. Check method used.", LocalDateTime.now());
    }


    // 11. Reserved Username Exception
    @ExceptionHandler(ReservedUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleReservedUsername(ReservedUsernameException ex) {
        ex.printStackTrace();
        return new ErrorDTO("Username invalid: Reserved words.", LocalDateTime.now());
    }

    // 11. Banned Account  Exception
    @ExceptionHandler(ReservedUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBannedAccount(ReservedUsernameException ex) {
        ex.printStackTrace();
        return new ErrorDTO("This account has been banned and is currently unavailable.", LocalDateTime.now());
    }
}