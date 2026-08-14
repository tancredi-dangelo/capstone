package tancredidangelo.capstone.authentication.registration.registrationUser.UserRegistrationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import tancredidangelo.capstone.entities.tag.Tag;
import tancredidangelo.capstone.helpers.ForbiddenUsernamesList;

import java.time.LocalDate;
import java.util.List;

/// The creation of a new USER comes with the creation of a first account in the same instance

public record UserRegistrationRequestDTO(
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
        @Size(min = 6, max = 20)
        String username,

        @NotBlank(message = "Password is required.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*?&_-]{8,24}$",
                message = "Your password must be 8-24 characters and must contain at least: one lower case, one upper case, a number and a special character."
        )
        String password,

        @NotNull Boolean isPrivate,

        @Size(max = 150, message = "Your bio should be max.150 characters long.") String bio,

        List<Tag> tags) {


        // Check if username is in Forbidden List
        @JsonIgnore
        @AssertTrue(message = "This username is reserved and can't be used. Choose another one.")
        public boolean isValidUsername() {
                return !ForbiddenUsernamesList.isReserved(this.username);
        }
}

