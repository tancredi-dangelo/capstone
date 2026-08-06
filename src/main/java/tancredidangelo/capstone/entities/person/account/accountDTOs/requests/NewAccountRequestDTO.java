package tancredidangelo.capstone.entities.person.account.accountDTOs.requests;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import tancredidangelo.capstone.helpers.ForbiddenUsernamesList;

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

        MultipartFile file,

        @Size(max = 150, message = "Your bio should be max.150 characters long.") String bio,

        @NotNull Boolean isPrivate,

        List<String> tags) {


        // check if username is forbidden
        @JsonIgnore
        @AssertTrue(message = "This username is reserved and can't be used. Choose another one.")
        public boolean isValidUsername() {
                return !ForbiddenUsernamesList.isReserved(this.username);
        }
}
