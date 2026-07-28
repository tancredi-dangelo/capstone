package tancredidangelo.capstone.entities.person.account.accountDTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import tancredidangelo.capstone.entities.person.user.User;

import java.util.List;

public record NewAccountRequestDTO(

        @NotNull(message = "User data are mandatory.")
        @Valid User user,

        @NotBlank(message = "Username is required.")
        @Size(min = 6, max = 20)
        String username,

        @NotBlank(message = "Password is required. ")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$",
                message = "Your password must be 8-24 characters and must contain at least: one lower case, one upper case, a number and a special character (@$!%*?&)."
        )
        String password,


        List<String> tags) {
}
