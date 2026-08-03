package tancredidangelo.capstone.entities.post.postDTO.responses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses.SavedPostResponseDTO;

import java.time.LocalDateTime;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WritingResponseDTO.class, name = "WRITING"),
        @JsonSubTypes.Type(value = PhotoResponseDTO.class, name = "PHOTO"),
        @JsonSubTypes.Type(value = CarouselResponseDTO.class, name = "CAROUSEL"),
        @JsonSubTypes.Type(value = VideoResponseDTO.class, name = "VIDEO")
})

// GENERIC DTO INTERFACE WITH COMMON POST ATTRIBUTES

public sealed interface PostResponseDTO permits WritingResponseDTO, PhotoResponseDTO, CarouselResponseDTO, VideoResponseDTO {
    Long id();
    Account author();
    LocalDateTime timestamp();
    int likes();
    int comments();
    boolean isUpdated();
}
