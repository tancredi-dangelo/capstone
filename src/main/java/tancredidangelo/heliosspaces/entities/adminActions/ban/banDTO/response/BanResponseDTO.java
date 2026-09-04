package tancredidangelo.heliosspaces.entities.adminActions.ban.banDTO.response;

import tancredidangelo.heliosspaces.entities.adminActions.ban.Ban;

import java.time.LocalDateTime;

public record BanResponseDTO(
        Long banId,
        Long accountId,
        Long adminId,
        String reason,
        LocalDateTime startingDate,
        boolean isPermanent,
        LocalDateTime expiringDate,
        boolean isRevoked
) {
    public static BanResponseDTO fromEntity(Ban ban) {
        return new BanResponseDTO(
                ban.getId(),
                ban.getAccount().getId(),
                ban.getAdmin().getId(),
                ban.getReason(),
                ban.getStartingDate(),
                ban.isPermanent(),
                ban.getExpiringDate(),
                ban.isRevoked()
        );
    }
}
