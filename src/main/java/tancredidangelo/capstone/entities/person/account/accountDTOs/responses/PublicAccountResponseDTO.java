package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.stack.Account;

import java.util.List;

public record PublicAccountResponseDTO(
        Long id,
        String username,
        String profilePicUrl,
        String bio,
        Boolean isPrivate,
        List<String> tags,
        int followersCount,
        int followingCount,
        int postsCount
) {
    public static PublicAccountResponseDTO fromEntity(Account account) {
        return new PublicAccountResponseDTO(
                account.getId(),
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.isPrivate(),
                account.getTags() != null ? account.getTags() : List.of(),
                account.getFollowers() != null ? account.getFollowers().size() : 0,
                account.getFollowing() != null ? account.getFollowing().size() : 0,
                account.getPosts() != null ? account.getPosts().size() : 0
        );
    }
}
