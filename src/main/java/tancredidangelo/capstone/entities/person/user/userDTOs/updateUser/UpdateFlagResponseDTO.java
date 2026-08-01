package tancredidangelo.capstone.entities.person.user.userDTOs.updateUser;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateFlagResponseDTO(@NotBlank UUID id) {
}
