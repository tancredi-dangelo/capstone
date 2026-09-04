package tancredidangelo.heliosspaces.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateWritingRequestDTO(
        @NotBlank @Size(max = 2000) String text
) {
}
