package tancredidangelo.capstone.entities.person.account.accountDTOs.updateAccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateAccountRequestDTO(

        @NotBlank(message = "Username is required.")
        @Size(min = 6, max = 20)
        String username,

        String profilePicUrl,

        String bio,

        List<String> tags
) {
}
