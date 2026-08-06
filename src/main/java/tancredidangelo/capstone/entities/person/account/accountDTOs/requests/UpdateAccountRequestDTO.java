package tancredidangelo.capstone.entities.person.account.accountDTOs.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateAccountRequestDTO(

        @NotBlank(message = "Username is required.")
        @Size(min = 6, max = 20)
        String username,

        String bio,

        @NotNull Boolean isPrivate,

        List<String> tags
) {
}
