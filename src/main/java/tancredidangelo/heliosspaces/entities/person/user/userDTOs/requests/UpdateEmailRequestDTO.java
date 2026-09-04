package tancredidangelo.heliosspaces.entities.person.user.userDTOs.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(

        @NotBlank(message = "Email is required.")
        @Email(message = "Provide a valid Email address.")
        String email
) {
}
