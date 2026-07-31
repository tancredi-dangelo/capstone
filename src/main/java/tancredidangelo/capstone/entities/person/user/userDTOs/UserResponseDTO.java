package tancredidangelo.capstone.entities.person.user.userDTOs;



import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthdate,
        String country,
        boolean isFlagged
) {
}
