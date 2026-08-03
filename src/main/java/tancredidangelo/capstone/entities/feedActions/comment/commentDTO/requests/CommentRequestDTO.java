package tancredidangelo.capstone.entities.feedActions.comment.commentDTO.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(
        @NotNull Long postId,
        @NotBlank @Size(max = 1000, message = "Comment text cannot exceed 1000 characters.") String text
) {
}

