package tancredidangelo.capstone.entities.savedPost.savedPostDTO.requests;

import jakarta.validation.constraints.NotNull;

public record SavedPostRequestDTO(
        @NotNull Long postId
        ) {
}
