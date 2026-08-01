package tancredidangelo.capstone.entities.person.user.userDTOs.updateUser;

import jakarta.validation.constraints.NotBlank;

public record UpdateFlagRequestDTO(@NotBlank boolean flagValue) {
}
