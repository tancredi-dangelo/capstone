package tancredidangelo.capstone.entities.adminActions.flag.flagDTO;

import tancredidangelo.capstone.entities.adminActions.flag.Flag;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlagResponseDTO(
        Long flagId,
        UUID userId,
        Long adminId,
        LocalDateTime timestamp,
        String reason
) {
    public static FlagResponseDTO fromEntity(Flag flag) {
        return new FlagResponseDTO(
                flag.getId(),
                flag.getUser().getId(),
                flag.getAdmin().getId(),
                flag.getTimestamp(),
                flag.getReason()
        );
    }
}
