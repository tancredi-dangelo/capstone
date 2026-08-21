package tancredidangelo.capstone.entities.feedActions.follow.followDTO.response;

import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

public record FollowPendingResponseDTO(
        PublicAccountResponseDTO follower,
        PublicAccountResponseDTO followed,
        String followStatus,
        LocalDateTime requestDate
) {
    public static FollowPendingResponseDTO fromEntity(Follow follow) {
        return new FollowPendingResponseDTO(
                PublicAccountResponseDTO.fromEntity(follow.getFollower()),
                PublicAccountResponseDTO.fromEntity(follow.getFollowed()),
                follow.getFollowStatus().name(),
                follow.getRequestDate()
        );
    }
}
