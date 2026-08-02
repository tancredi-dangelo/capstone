package tancredidangelo.capstone.helpers;

import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.postDTO.responses.*;
import tancredidangelo.capstone.entities.postSubclasses.carousel.Carousel;
import tancredidangelo.capstone.entities.postSubclasses.photo.Photo;
import tancredidangelo.capstone.entities.postSubclasses.video.Video;
import tancredidangelo.capstone.entities.postSubclasses.writing.Writing;

public class ConverterPostDTO {
    /// Polymorphic mapper for Post subclasses
    public static PostResponseDTO convertToDTO(Post post) {
        return switch (post) {
            case Writing writing   -> WritingResponseDTO.fromEntity(writing);
            case Photo photo       -> PhotoResponseDTO.fromEntity(photo);
            case Carousel carousel -> CarouselResponseDTO.fromEntity(carousel);
            case Video video       -> VideoResponseDTO.fromEntity(video);
            default -> throw new IllegalArgumentException("Unknown post subclass type: " + post.getClass().getName());
        };
    }
}
