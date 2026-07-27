package tancredidangelo.capstone.errorHandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    // 6. Message Not Readable (Malformed JSON) -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ErrorDTO("Malformed JSON request or invalid data types provided.", LocalDateTime.now());
    }

    // 7. 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleGenericError(Exception ex) {
        ex.printStackTrace();
        return new ErrorDTO("Internal Server Error: Something went wrong, try again later.", LocalDateTime.now());
    }
}
