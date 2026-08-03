package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.time.LocalDateTime;
import java.util.List;

public record AdminAccountResponseDTO(
        Long id,
        String username,
        LocalDateTime dateOfRegistration,
        List<String> tags,
        boolean isBanned,
        int followersCount,
        int followingCount,
        int postsCount
) {
    public static AdminAccountResponseDTO fromEntity(Account account) {
        return new AdminAccountResponseDTO(
                account.getId(),
                account.getUsername(),
                account.getDateOfRegistration(),
                account.getTags() != null ? account.getTags() : List.of(),
                account.isBanned(),
                account.getFollowers() != null ? account.getFollowers().size() : 0,
                account.getFollowing() != null ? account.getFollowing().size() : 0,
                account.getPosts() != null ? account.getPosts().size() : 0
        );
    }
}