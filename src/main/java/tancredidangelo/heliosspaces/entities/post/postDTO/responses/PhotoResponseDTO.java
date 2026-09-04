package tancredidangelo.heliosspaces.entities.post.postDTO.responses;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import tancredidangelo.heliosspaces.entities.post.postSubclasses.photo.Photo;

import java.time.LocalDateTime;

public record PhotoResponseDTO(

        @NotNull Long id,
        @NotNull String authorUsername,
        @NotBlank @URL(message = "Url must be valid") String profilePicUrl,
        @NotBlank @URL(message = "Url must be valid") String photoUrl,
        @Size(max = 2200, message = "Caption shouldn't exceed 2200 characters.") String caption,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Min(0) int likesCount,
        @Min(0) int commentsCount,
        boolean isUpdated

) implements PostResponseDTO {

    public static PhotoResponseDTO fromEntity(Photo photo) {
        return new PhotoResponseDTO(
                photo.getId(),
                photo.getAuthor().getUsername(),
                photo.getAuthor().getProfilePicUrl(),
                photo.getPhotoUrl(),
                photo.getCaption(),
                photo.getTimestamp(),
                photo.getLikes().size(),
                photo.getComments().size(),
                photo.isUpdated()
        );
    }
}
