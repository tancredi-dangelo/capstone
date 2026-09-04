package tancredidangelo.heliosspaces.entities.adminActions.flag.flagDTO;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateFlagRequestDTO(
        @NotNull UUID userId,
        @NotNull Long adminId,
        @NotNull String reason
) {
}
