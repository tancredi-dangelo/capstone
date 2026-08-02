package tancredidangelo.capstone.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public record CreateCarouselRequestDTO(

        @NotNull Long authorId,
        @Size(max = 2200) String caption,
        @NotEmpty @Size(min = 2, max = 10) List<@NotBlank @URL String> mediaUrls
) {
}
