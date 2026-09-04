package tancredidangelo.heliosspaces.entities.tag.tagDTO.response;

import tancredidangelo.heliosspaces.entities.tag.Tag;

public record TagResponseDTO(
        Long id,
        String title
) {
    public static TagResponseDTO fromEntity(Tag tag) {
        return new TagResponseDTO(
                tag.getId(),
                tag.getTitle()
        );
    }
}
