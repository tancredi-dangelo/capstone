package tancredidangelo.heliosspaces.entities.post.postDTO.requests.update;

import jakarta.validation.constraints.Size;

public record UpdatePostRequestDTO(
        @Size(max = 2000) String caption,
        @Size(max = 2000) String text
) {
}
