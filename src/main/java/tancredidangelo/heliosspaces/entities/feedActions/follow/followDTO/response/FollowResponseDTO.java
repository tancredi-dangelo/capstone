package tancredidangelo.heliosspaces.entities.feedActions.follow.followDTO.response;

import tancredidangelo.heliosspaces.entities.feedActions.follow.Follow;
import tancredidangelo.heliosspaces.entities.person.account.accountDTOs.responses.PublicAccountResponseDTO;

import java.time.LocalDateTime;

public record FollowResponseDTO(
        PublicAccountResponseDTO follower,
        PublicAccountResponseDTO followed,
        String followStatus,
        LocalDateTime requestDate,
        LocalDateTime responseDate
) {
    public static FollowResponseDTO fromEntity(Follow follow) {

        return new FollowResponseDTO(
                PublicAccountResponseDTO.fromEntity(follow.getFollower()),
                PublicAccountResponseDTO.fromEntity(follow.getFollowed()),
                follow.getFollowStatus().name(),
                follow.getRequestDate(),
                follow.getResponseDate()
        );
    }
}
