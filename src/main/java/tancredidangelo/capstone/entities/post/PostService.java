package tancredidangelo.capstone.entities.post;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.post.postDTO.requests.update.UpdatePostRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.*;
import tancredidangelo.capstone.helpers.ConverterPostDTO;
import tancredidangelo.capstone.entities.post.postSubclasses.writing.Writing;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.time.LocalDateTime;

import static tancredidangelo.capstone.helpers.ConverterPostDTO.convertToDTO;

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



    /// Get Feed
    public Page<PostResponseDTO> getFeed(Long accountId, LocalDateTime since, Pageable pageable) {

        LocalDateTime effectiveSince = (since != null) ? since : LocalDateTime.now().minusHours(24);

        Page<Post> rawPosts = this.postRepository.findFeedForAccount(accountId, effectiveSince, pageable);

        return rawPosts.map(ConverterPostDTO::convertToDTO);
    }


    /// Update post by id
    public PostResponseDTO updateById(Long id, UpdatePostRequestDTO payload) {

        Post found = findById(id);

        if (found instanceof Writing) {
            ((Writing) found).setText(payload.text());
        } else {
            found.setCaption(payload.caption());
        }

        found.setUpdated(true);
        Post saved = save(found);

        return convertToDTO(saved);
    }



    /// Delete post by id
    @Transactional
    public void deleteById(Long id) {
        Post found = findById(id);
        this.postRepository.delete(found);
        log.info("Post with ID {} successfully deleted.", id);
    }

}