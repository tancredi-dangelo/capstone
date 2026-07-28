package tancredidangelo.capstone.entities.person.user.userDTOs;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record NewUserResponseDTO(@NotBlank UUID id) {
}
