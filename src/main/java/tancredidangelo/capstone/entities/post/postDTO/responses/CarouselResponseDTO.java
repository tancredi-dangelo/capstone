package tancredidangelo.capstone.entities.post.postDTO.responses;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postSubclasses.carousel.Carousel;

import java.time.LocalDateTime;
import java.util.List;

public record CarouselResponseDTO(

        @NotNull Long id,
        @NotNull String authorUsername,
        @NotBlank @URL String profilePicUrl,
        @Size(max = 2200) String caption,
        @Size(min = 2, max = 10) List<@NotBlank @URL String> mediaUrls,
        @Min(2) @Max(10) int length,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Min(0) int likesCount,
        @Min(0) int commentsCount,
        boolean isUpdated

) implements PostResponseDTO {

    public static CarouselResponseDTO fromEntity(Carousel carousel) {
        return new CarouselResponseDTO(
                carousel.getId(),
                carousel.getAuthor().getUsername(),
                carousel.getAuthor().getProfilePicUrl(),
                carousel.getCaption(),
                carousel.getMediaUrls(),
                carousel.getLength(),
                carousel.getTimestamp(),
                carousel.getLikes().size(),
                carousel.getComments().size(),
                carousel.isUpdated()
        );
    }
}
