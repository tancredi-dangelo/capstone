package tancredidangelo.capstone.entities.post.postDTO;

import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

public record VideoResponseDTO(
        Long id,
        Account author,
        String caption,
        String videoUrl,
        int durationSeconds,
        LocalDateTime timestamp,
        int likes,
        int comments,
        boolean isUpdated
) implements PostResponseDTO {
}