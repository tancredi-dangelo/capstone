package tancredidangelo.capstone.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreatePhotoRequestDTO(
        @NotNull Long authorId,
        @Size(max = 2200) String caption,
        @NotBlank  @URL String photoUrl
) {
}
