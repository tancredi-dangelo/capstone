package tancredidangelo.heliosspaces.entities.feedActions.like.likeDTO.request;

import jakarta.validation.constraints.NotNull;

public record LikePostRequestDTO(
        @NotNull Long postId
) {
}
