package tancredidangelo.capstone.entities.tag.tagDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestDTO(@NotBlank @Size(min = 1, max = 20) String title) {
}
