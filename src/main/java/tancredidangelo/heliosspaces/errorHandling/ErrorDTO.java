package tancredidangelo.heliosspaces.errorHandling;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(String message, LocalDateTime timestamp, List<String> errorsList) {
    public ErrorDTO(String message, LocalDateTime timestamp) {
        this(message, timestamp, null);
    }
}
