package tancredidangelo.capstone.authentication.login.LoginDTO;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(@NotBlank String username, @NotBlank String password) {
}
