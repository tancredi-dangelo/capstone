package tancredidangelo.capstone.entities.postSubclasses.writing.writingDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdateWritingRequestDTO(
        @NotBlank @Size(max = 1500) String text
) {
}
