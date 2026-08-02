package tancredidangelo.capstone.entities.post.postDTO;

import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.postSubclasses.writing.Writing;

import java.time.LocalDateTime;

public record WritingResponseDTO(

        Long id,
        Account author,
        String text,
        LocalDateTime timestamp,
        int likes,
        int comments,
        boolean isUpdated

) implements PostResponseDTO {

    public static WritingResponseDTO fromEntity(Writing writing) {
        return new WritingResponseDTO(
                writing.getId(),
                writing.getAuthor(),
                writing.getText(),
                writing.getTimestamp(),
                writing.getLikes().size(),
                writing.getComments().size(),
                writing.isUpdated()
        );
    }

}
