package tancredidangelo.heliosspaces.entities.feedActions.follow.followDTO.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDTO(
        @NotNull Long followedId
) {
}
