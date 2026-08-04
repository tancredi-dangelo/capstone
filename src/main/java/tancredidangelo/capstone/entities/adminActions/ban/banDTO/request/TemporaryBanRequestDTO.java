package tancredidangelo.capstone.entities.adminActions.ban.banDTO.request;



import java.time.LocalDateTime;

public record TemporaryBanRequestDTO(
        Long accountId,
        Long adminId,
        String reason,
        LocalDateTime startingDate,
        LocalDateTime expiringDate
) {
}
