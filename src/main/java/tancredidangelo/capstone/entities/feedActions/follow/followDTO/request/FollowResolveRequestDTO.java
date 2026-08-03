package tancredidangelo.capstone.entities.feedActions.follow.followDTO.request;

import jakarta.validation.constraints.NotNull;

public record FollowResolveRequestDTO(
        @NotNull Long followId,
        @NotNull boolean value) {
}
