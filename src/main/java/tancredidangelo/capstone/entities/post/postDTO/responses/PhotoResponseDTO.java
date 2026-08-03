package tancredidangelo.capstone.entities.post.postDTO.responses;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postSubclasses.photo.Photo;

import java.time.LocalDateTime;

public record PhotoResponseDTO(

        @NotNull Long id,
        @NotNull Account author,
        @Size(max = 2200, message = "La didascalia non può superare i 2200 caratteri") String caption,
        @NotBlank @URL(message = "L'URL della foto deve essere valido") String photoUrl,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Min(0) int likes,
        @Min(0) int comments,
        boolean isUpdated

) implements PostResponseDTO {

    public static PhotoResponseDTO fromEntity(Photo photo) {
        return new PhotoResponseDTO(
                photo.getId(),
                photo.getAuthor(),
                photo.getPhotoUrl(),
                photo.getCaption(),
                photo.getTimestamp(),
                photo.getLikes().size(),
                photo.getComments().size(),
                photo.isUpdated()
        );
    }
}
