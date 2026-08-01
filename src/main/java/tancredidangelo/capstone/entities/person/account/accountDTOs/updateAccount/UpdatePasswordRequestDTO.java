package tancredidangelo.capstone.entities.person.account.accountDTOs.updateAccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequestDTO(

        @NotBlank(message = "Password is required.")
        String oldPassword,

        @NotBlank(message = "Password is required. ")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$",
                message = "Your password must be 8-24 characters and must contain at least: one lower case, one upper case, a number and a special character (@$!%*?&)."
        )
        String newPassword) {
}
