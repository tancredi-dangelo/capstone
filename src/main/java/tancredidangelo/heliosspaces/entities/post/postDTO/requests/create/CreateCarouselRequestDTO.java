package tancredidangelo.heliosspaces.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateCarouselRequestDTO(

        @Size(max = 2200) String caption,
        @NotEmpty @Size(min = 2, max = 10) List<MultipartFile> files
) {
}
