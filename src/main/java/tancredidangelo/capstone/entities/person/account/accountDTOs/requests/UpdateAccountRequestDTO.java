package tancredidangelo.capstone.entities.person.account.accountDTOs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tancredidangelo.capstone.entities.tag.Tag;

import java.util.List;

public record UpdateAccountRequestDTO(

        @NotBlank(message = "Username is required.")
        @Size(min = 6, max = 20)
        String username,

        String bio,

        @JsonProperty("isPrivate") @NotNull Boolean isPrivate,

        List<Tag> tags
) {
}
