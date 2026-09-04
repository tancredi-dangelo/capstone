package tancredidangelo.heliosspaces.entities.person.account.accountDTOs.requests;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import tancredidangelo.heliosspaces.entities.tag.tagDTO.response.TagResponseDTO;
import tancredidangelo.heliosspaces.helpers.ForbiddenUsernamesList;

import java.util.List;

public record NewAccountRequestDTO(

        @NotBlank(message = "Username is required.")
        @Size(min = 6, max = 20)
        String username,

        @NotBlank(message = "Password is required.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{8,24}$",
                message = "Your password must be 8-24 characters and must contain at least: one lower case, one upper case, a number and a special character."
        )
        String password,


        @Size(max = 150, message = "Your bio should be max.150 characters long.") String bio,

        @JsonProperty("isPrivate") @NotNull Boolean isPrivate,

        List<TagResponseDTO> tags) {


        // check if username is forbidden
        @JsonIgnore
        @AssertTrue(message = "This username is reserved and can't be used. Choose another one.")
        public boolean isValidUsername() {
                return !ForbiddenUsernamesList.isReserved(this.username);
        }
}
