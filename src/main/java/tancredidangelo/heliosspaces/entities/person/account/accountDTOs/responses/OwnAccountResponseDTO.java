package tancredidangelo.heliosspaces.entities.person.account.accountDTOs.responses;

import tancredidangelo.heliosspaces.entities.person.account.AccountRoles;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.tag.tagDTO.response.TagResponseDTO;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OwnAccountResponseDTO(
        Long id,
        UUID userId,
        String firstName,
        String lastName,
        LocalDate birthdate,
        String email,
        String country,
        String username,
        String profilePicUrl,
        String bio,
        Boolean isPrivate,
        AccountRoles role,
        List<TagResponseDTO> tags,
        LocalDateTime dateOfRegistration,
        int followersCount,
        int followingCount,
        int postsCount,
        int savedPostsCount,
        List<Long> accountsId
) {
    public static OwnAccountResponseDTO fromEntity(Account account) {

        List<TagResponseDTO> tags = List.of();

        if (account.getTags() != null) {
            tags = account.getTags().stream().map(TagResponseDTO::fromEntity).toList();
        }

        return new OwnAccountResponseDTO(
                account.getId(),
                account.getUser().getId(),
                account.getUser().getFirstName(),
                account.getUser().getLastName(),
                account.getUser().getBirthdate(),
                account.getUser().getEmail(),
                account.getUser().getCountry(),
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.isPrivate(),
                account.getRole(),
                tags,
                account.getDateOfRegistration(),
                account.getFollowers().size(),
                account.getFollowing().size(),
                account.getPosts().size(),
                account.getSavedPosts().size(),
                account.getUser().getUserAccounts() != null ? (account.getUser().getUserAccounts()).stream().map(account1 -> account1.getId()).toList() : null
        );
    }
}
