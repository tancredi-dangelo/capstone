package tancredidangelo.heliosspaces.entities.person.user.userDTOs.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateFlagRequestDTO(@NotBlank boolean flagValue) {
}
