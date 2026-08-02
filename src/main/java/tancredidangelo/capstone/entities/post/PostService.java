package tancredidangelo.capstone.entities.post;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.post.postDTO.responses.*;
import tancredidangelo.capstone.entities.postSubclasses.carousel.Carousel;
import tancredidangelo.capstone.entities.postSubclasses.photo.Photo;
import tancredidangelo.capstone.entities.postSubclasses.video.Video;
import tancredidangelo.capstone.entities.postSubclasses.writing.Writing;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.time.LocalDateTime;

@Service
@Slf4j

public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ------------------ METHODS --------------------------------------

    /// Save generic post
    public Post save(Post post) {
        return this.postRepository.save(post);
    }


    /// Find post by id
    public Post findById(Long id) {
        return this.postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post with ID " + id + " not found."));
    }


    /// Get Single Post DTO (Polymorphic)
    public PostResponseDTO findDTOById(Long id) {
        Post found = findById(id);
        return convertToDTO(found);
    }


    /// Get Feed
    public Page<PostResponseDTO> getFeed(Long accountId, LocalDateTime since, Pageable pageable) {

        LocalDateTime effectiveSince = (since != null) ? since : LocalDateTime.now().minusHours(24);

        Page<Post> rawPosts = this.postRepository.findFeedForAccount(accountId, effectiveSince, pageable);

        return rawPosts.map(this::convertToDTO);
    }

    /// Delete post by id
    @Transactional
    public void deleteById(Long id) {
        Post found = findById(id);
        this.postRepository.delete(found);
        log.info("Post with ID {} successfully deleted.", id);
    }



    // ------------------ HELPER DTO MAPPER -----------------------------------------------------------

    /// Polymorphic mapper for Post subclasses
    public PostResponseDTO convertToDTO(Post post) {
        return switch (post) {
            case Writing writing   -> WritingResponseDTO.fromEntity(writing);
            case Photo photo       -> PhotoResponseDTO.fromEntity(photo);
            case Carousel carousel -> CarouselResponseDTO.fromEntity(carousel);
            case Video video       -> VideoResponseDTO.fromEntity(video);
            default -> throw new IllegalArgumentException("Unknown post subclass type: " + post.getClass().getName());
        };
    }
}