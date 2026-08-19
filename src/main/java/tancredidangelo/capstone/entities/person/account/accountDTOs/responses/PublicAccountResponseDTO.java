package tancredidangelo.capstone.entities.person.account.accountDTOs.responses;

import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.tag.Tag;
import tancredidangelo.capstone.entities.tag.tagDTO.response.TagResponseDTO;

import java.util.List;

public record PublicAccountResponseDTO(
        Long id,
        String username,
        String profilePicUrl,
        String bio,
        Boolean isPrivate,
        List<TagResponseDTO> tags
) {
    public static PublicAccountResponseDTO fromEntity(Account account) {

        List<TagResponseDTO> tags = List.of();

        if (account.getTags() != null) {
            tags = account.getTags().stream().map(TagResponseDTO::fromEntity).toList();
        }

        return new PublicAccountResponseDTO(
                account.getId(),
                account.getUsername(),
                account.getProfilePicUrl(),
                account.getBio(),
                account.getIsPrivate(),
                tags
        );
    }
}
