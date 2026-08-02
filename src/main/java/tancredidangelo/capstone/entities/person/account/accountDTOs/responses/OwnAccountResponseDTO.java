package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.AccountRoles;
import tancredidangelo.capstone.entities.person.account.stack.Account;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OwnAccountResponseDTO(
        Long id,
        UUID userId,
        String email,
        String username,
        String profilePicUrl,
        String bio,
        AccountRoles role,
        List<String> tags,
        LocalDateTime dateOfRegistration,
        int followersCount,
        int followingCount,
        int postsCount,
        int savedPostsCount
) {
    public static OwnAccountResponseDTO fromEntity(Account account) {
        return new OwnAccountResponseDTO(
                account.getId(),
                account.getUser() != null ? account.getUser().getId() : null,
                account.getUser() != null ? account.getUser().getEmail() : null,
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.getRole(),
                account.getTags() != null ? account.getTags() : List.of(),
                account.getDateOfRegistration(),
                account.getFollowers() != null ? account.getFollowers().size() : 0,
                account.getFollowing() != null ? account.getFollowing().size() : 0,
                account.getPosts() != null ? account.getPosts().size() : 0,
                account.getSavedPosts() != null ? account.getSavedPosts().size() : 0
        );
    }
}
