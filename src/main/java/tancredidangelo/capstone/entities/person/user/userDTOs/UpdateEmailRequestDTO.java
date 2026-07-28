package tancredidangelo.capstone.entities.person.user.userDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(

        @NotBlank

        @NotBlank(message = "Email is required.")
        @Email(message = "Provide a valid Email address.")
        String email
) {
}
