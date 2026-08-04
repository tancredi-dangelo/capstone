package tancredidangelo.capstone.entities.adminActions.ban.banDTO.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PermanentBanRequestDTO(
        @NotNull Long accountId,
        @NotNull Long adminId,
        String reason,
        LocalDateTime startingDate
) {
}
