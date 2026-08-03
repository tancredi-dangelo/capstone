package tancredidangelo.capstone.entities.feedActions.follow.followDTO.response;

import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.feedActions.follow.FollowStatus;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

public record FollowResponseDTO(
        Account follower,
        Account followed,
        String followStatus,
        LocalDateTime requestDate,
        LocalDateTime responseDate
) {
    public static FollowResponseDTO fromEntity(Follow follow) {

        follow.setFollowStatus(FollowStatus.ACCEPTED);

        return new FollowResponseDTO(
                follow.getFollower(),
                follow.getFollowed(),
                follow.getFollowStatus().name(),
                follow.getRequestDate(),
                follow.getResponseDate()
        );
    }
}
