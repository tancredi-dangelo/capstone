package tancredidangelo.heliosspaces.entities.feedActions.like.likeDTO.response;

import tancredidangelo.heliosspaces.entities.feedActions.like.Like;

import java.time.LocalDateTime;

public record LikeResponseDTO(
        Long id,
        Long authorId,
        Long postId,
        Long commentId,
        LocalDateTime timestamp
) {
    public static LikeResponseDTO fromEntity(Like like) {
        return new LikeResponseDTO(
                like.getId(),
                like.getAuthor().getId(),
                like.getPost() != null ? like.getPost().getId() : null,
                like.getComment() != null ? like.getComment().getId() : null,
                like.getTimestamp()
        );
    }
}
