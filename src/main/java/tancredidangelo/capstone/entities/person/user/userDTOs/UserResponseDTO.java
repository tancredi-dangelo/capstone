package tancredidangelo.capstone.entities.person.user.userDTOs;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Setter;

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
