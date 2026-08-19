package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.AccountRoles;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.tag.Tag;
import tancredidangelo.capstone.entities.tag.tagDTO.response.TagResponseDTO;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OwnAccountResponseDTO(
        Long id,
        String username,
        String profilePicUrl,
        String bio,
        Boolean isPrivate,
        AccountRoles role,
        List<TagResponseDTO> tags,
        LocalDateTime dateOfRegistration,
        List<Long> accountsId
) {
    public static OwnAccountResponseDTO fromEntity(Account account) {

        List<TagResponseDTO> tags = List.of();

        if (account.getTags() != null) {
            tags = account.getTags().stream().map(TagResponseDTO::fromEntity).toList();
        }

        return new OwnAccountResponseDTO(
                account.getId(),
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.getIsPrivate(),
                account.getRole(),
                tags,
                account.getDateOfRegistration(),
                account.getUser().getUserAccounts() != null ? (account.getUser().getUserAccounts()).stream().map(account1 -> account1.getId()).toList() : null
        );
    }
}
