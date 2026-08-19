package tancredidangelo.capstone.entities.feedActions.follow.followDTO.response;

import tancredidangelo.capstone.entities.feedActions.follow.Follow;
import tancredidangelo.capstone.entities.feedActions.follow.FollowStatus;
import tancredidangelo.capstone.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;

public record FollowResponseDTO(
        PublicAccountResponseDTO follower,
        PublicAccountResponseDTO followed,
        String followStatus,
        LocalDateTime requestDate,
        LocalDateTime responseDate
) {
    public static FollowResponseDTO fromEntity(Follow follow) {

        follow.setFollowStatus(FollowStatus.ACCEPTED);

        return new FollowResponseDTO(
                PublicAccountResponseDTO.fromEntity(follow.getFollower()),
                PublicAccountResponseDTO.fromEntity(follow.getFollowed()),
                follow.getFollowStatus().name(),
                follow.getRequestDate(),
                follow.getResponseDate()
        );
    }
}
