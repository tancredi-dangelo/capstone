package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.AccountRoles;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.tag.Tag;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OwnAccountResponseDTO(
        String username,
        String profilePicUrl,
        String bio,
        Boolean isPrivate,
        AccountRoles role,
        List<Tag> tags,
        LocalDateTime dateOfRegistration,
        int followersCount,
        int followingCount,
        int postsCount,
        int savedPostsCount,
        List<Long> accountsId
) {
    public static OwnAccountResponseDTO fromEntity(Account account) {
        return new OwnAccountResponseDTO(
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.getIsPrivate(),
                account.getRole(),
                account.getTags() != null ? account.getTags() : List.of(),
                account.getDateOfRegistration(),
                account.getFollowers() != null ? account.getFollowers().size() : 0,
                account.getFollowing() != null ? account.getFollowing().size() : 0,
                account.getPosts() != null ? account.getPosts().size() : 0,
                account.getSavedPosts() != null ? account.getSavedPosts().size() : 0,
                account.getUser().getUserAccounts() != null ? (account.getUser().getUserAccounts()).stream().map(account1 -> account1.getId()).toList() : null
        );
    }
}
