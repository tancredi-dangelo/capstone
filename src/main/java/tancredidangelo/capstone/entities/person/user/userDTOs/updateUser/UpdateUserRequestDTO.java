package tancredidangelo.capstone.entities.person.user.userDTOs.updateUser;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequestDTO(
        @NotBlank(message = "First Name is required.")
        @Size(min = 2, max = 50, message = "It has to be 2 to 50 characters long.")
        String firstName,

        @NotBlank(message = "Last Name is required.")
        @Size(min = 2, max = 50, message = "It has to be 2 to 50 characters long.")
        String lastName,

        @NotNull(message = "Birthdate is required.")
        @Past(message = "Your birthdate must be in the past!")
        LocalDate birthdate,

        @NotBlank(message = "Country is required.")
        @Size(min = 2, max = 100, message = "It has to be 2 to 100 characters long.")
        String country) {
}
