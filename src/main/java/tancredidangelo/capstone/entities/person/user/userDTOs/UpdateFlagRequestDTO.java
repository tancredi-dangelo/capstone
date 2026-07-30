package tancredidangelo.capstone.entities.person.user.userDTOs;

import jakarta.validation.constraints.NotBlank;

public record UpdateFlagRequestDTO(@NotBlank boolean flagValue) {
}
