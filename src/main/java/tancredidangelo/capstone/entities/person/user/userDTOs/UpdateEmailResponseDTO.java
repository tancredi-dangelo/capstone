package tancredidangelo.capstone.entities.person.user.userDTOs;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateEmailResponseDTO(@NotBlank UUID id) {
}
