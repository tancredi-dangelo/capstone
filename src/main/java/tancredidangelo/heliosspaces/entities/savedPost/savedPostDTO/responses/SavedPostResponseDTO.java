package tancredidangelo.heliosspaces.entities.savedPost.savedPostDTO.responses;

import tancredidangelo.heliosspaces.entities.post.postDTO.responses.PostResponseDTO;
import tancredidangelo.heliosspaces.entities.savedPost.SavedPost;
import tancredidangelo.heliosspaces.helpers.ConverterPostDTO;

import java.time.LocalDateTime;


public record SavedPostResponseDTO(
        Long id,
        LocalDateTime timestamp,
        PostResponseDTO post


) {
    public static SavedPostResponseDTO fromEntity(SavedPost savedPost) {
        return new SavedPostResponseDTO(
                savedPost.getId(),
                savedPost.getTimestamp(),
                ConverterPostDTO.convertToDTO(savedPost.getPost()));
    }
}
