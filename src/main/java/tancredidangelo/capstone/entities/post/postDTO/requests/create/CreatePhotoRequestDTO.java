package tancredidangelo.capstone.entities.post.postDTO.requests.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

public record CreatePhotoRequestDTO(
        @Size(max = 2200) String caption,
        @NotNull MultipartFile file
        ) {
}
