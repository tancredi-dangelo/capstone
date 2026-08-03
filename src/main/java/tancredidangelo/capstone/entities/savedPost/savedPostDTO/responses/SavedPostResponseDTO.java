package tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses;

import tancredidangelo.capstone.entities.post.postDTO.responses.PostResponseDTO;
import tancredidangelo.capstone.entities.savedPost.SavedPost;
import tancredidangelo.capstone.helpers.ConverterPostDTO;
import tancredidangelo.capstone.helpers.ConverterPostDTO.*;

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
