package tancredidangelo.capstone.entities.feedActions.follow.followDTO.response;

import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

public record FollowResolvedResponseDTO(
        Account follower,
        Account followed,
        String followStatus,
        LocalDateTime requestDate,
        LocalDateTime responseDate
) {
    public static FollowResolvedResponseDTO fromEntity(Follow follow) {
        return new FollowResolvedResponseDTO(
                follow.getFollower(),
                follow.getFollowed(),
                follow.getFollowStatus().name(),
                follow.getRequestDate(),
                follow.getResponseDate()
        );
    }
}
