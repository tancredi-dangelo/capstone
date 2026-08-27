package tancredidangelo.capstone.entities.post.postDTO.responses;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postSubclasses.writing.Writing;

import java.time.LocalDateTime;

public record WritingResponseDTO(

        @NotNull Long id,
        @NotNull String authorUsername,
        @NotNull @URL String profilePicUrl,
        @NotBlank @Size(max = 2200, message = "Il testo non può superare i 2200 caratteri") String text,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Min(0) int likesCount,
        @Min(0) int commentsCount,
        boolean isUpdated

) implements PostResponseDTO {

    public static WritingResponseDTO fromEntity(Writing writing) {
        return new WritingResponseDTO(
                writing.getId(),
                writing.getAuthor().getUsername(),
                writing.getAuthor().getProfilePicUrl(),
                writing.getText(),
                writing.getTimestamp(),
                writing.getLikes().size(),
                writing.getComments().size(),
                writing.isUpdated()
        );
    }

}
