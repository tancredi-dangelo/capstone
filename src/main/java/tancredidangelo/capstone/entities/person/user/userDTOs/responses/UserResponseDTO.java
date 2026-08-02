package tancredidangelo.capstone.entities.person.user.userDTOs.responses;

import tancredidangelo.capstone.entities.person.user.stack.User;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        LocalDate birthdate,
        String country
) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthdate(),
                user.getCountry()
        );
    }
}
