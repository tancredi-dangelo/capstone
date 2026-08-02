package tancredidangelo.capstone.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tancredidangelo.capstone.entities.person.account.stack.Account;


public record CreateWritingRequestDTO(
        @NotNull Account author,
        @NotBlank @Size(max = 2000) String text
) {
}
