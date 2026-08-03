package tancredidangelo.capstone.entities.feedActions.comment.commentDTO.responses;

import tancredidangelo.capstone.entities.feedActions.comment.Comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        Long postId,
        Long authorId,
        String authorUsername,
        String authorProfilePic,
        String text,
        LocalDateTime timestamp,
        int likesCount
) {
    public static CommentResponseDTO fromEntity(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getAuthor().getProfilePicUrl(),
                comment.getText(),
                comment.getTimestamp(),
                comment.getLikes() != null ? comment.getLikes().size() : 0
        );
    }
}
