package tancredidangelo.capstone.authentication.registration.registrationAdmin.adminRegistrationDTO;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AdminRegistrationRequestDTO(

        @NotBlank(message = "First Name is required.")
        @Size(min = 2, max = 50, message = "It has to be 2 to 50 characters long.")
        String firstName,

        @NotBlank(message = "Last Name is required.")
        @Size(min = 2, max = 50, message = "It has to be 2 to 50 characters long.")
        String lastName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Provide a valid Email address.")
        String email,

        @NotNull(message = "Birthdate is required.")
        @Past(message = "Your birthdate must be in the past!")
        LocalDate birthdate,

        @NotBlank(message = "Country is required.")
        @Size(min = 2, max = 100, message = "It has to be 2 to 100 characters long.")
        String country,

        @NotBlank(message = "Username is required.")
        String username,

        @NotBlank(message = "Password is required.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{8,24}$",
                message = "Your password must be 8-24 characters and must contain at least: one lower case, one upper case, a number and a special character."
        )
        String password) {
}
