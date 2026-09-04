package tancredidangelo.heliosspaces.helpers;

import org.hibernate.Hibernate;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.postDTO.responses.*;
import tancredidangelo.heliosspaces.entities.post.postSubclasses.carousel.Carousel;
import tancredidangelo.heliosspaces.entities.post.postSubclasses.photo.Photo;
import tancredidangelo.heliosspaces.entities.post.postSubclasses.video.Video;
import tancredidangelo.heliosspaces.entities.post.postSubclasses.writing.Writing;

public class ConverterPostDTO {

    /// Polymorphic mapper for Post subclasses
    public static PostResponseDTO convertToDTO(Post post) {

        // unwrap entity from Hibernate proxy
        Object unproxied = Hibernate.unproxy(post);

        return switch (unproxied) {
            case Writing writing   -> WritingResponseDTO.fromEntity(writing);
            case Photo photo       -> PhotoResponseDTO.fromEntity(photo);
            case Carousel carousel -> CarouselResponseDTO.fromEntity(carousel);
            case Video video       -> VideoResponseDTO.fromEntity(video);
            default -> throw new IllegalArgumentException("Unknown post subclass type: " + post.getClass().getName());
        };
    }
}
