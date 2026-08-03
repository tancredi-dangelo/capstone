package tancredidangelo.capstone.entities.feedActions.follow.followDTO.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDTO(
        @NotNull Long followerId,
        @NotNull Long followedId
) {
}
