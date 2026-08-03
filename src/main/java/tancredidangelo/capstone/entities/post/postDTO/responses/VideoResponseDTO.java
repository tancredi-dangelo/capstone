package tancredidangelo.capstone.entities.post.postDTO.responses;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postSubclasses.video.Video;

import java.time.LocalDateTime;

public record VideoResponseDTO(

        @NotNull Long id,
        @NotNull Account author,
        @Size(max = 2200) String caption,
        @NotBlank @URL(message = "L'URL del video deve essere valido") String videoUrl,
        @Positive(message = "La durata in secondi deve essere un numero positivo") int durationSeconds,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Min(0) int likes,
        @Min(0) int comments,
        boolean isUpdated

) implements PostResponseDTO {

    public static VideoResponseDTO fromEntity(Video video) {
        return new VideoResponseDTO(
                video.getId(),
                video.getAuthor(),
                video.getVideoUrl(),
                video.getCaption(),
                video.getDurationSeconds(),
                video.getTimestamp(),
                video.getLikes().size(),
                video.getComments().size(),
                video.isUpdated()
        );
    }

}