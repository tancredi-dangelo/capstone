package tancredidangelo.capstone.entities.post;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PostService {

    /// dependency injection

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    // ------------------ methods --------------------------------------

    /// find post by id
    public Post findById(Long id) {
        return this.postRepository.findById(id).orElseThrow(()-> new NotFoundException("Post Not Found."));
    }


    /// get Feed
    LocalDateTime since = LocalDateTime.now().minusHours(24);
    public Page<Post> getFeed(Long accountId, LocalDateTime since, Pageable pageable) {
        return this.postRepository.findFeedForAccount(accountId, since, pageable);
    }


    /// delete post by id
    @Transactional
    public void deleteById(Long id) {
        Post found = findById(id);
        this.postRepository.delete(found);
        log.info("Post successfully deleted.");
    }
}
