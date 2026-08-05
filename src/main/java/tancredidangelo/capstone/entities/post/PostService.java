package tancredidangelo.capstone.entities.post;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.adminActions.ban.Ban;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.postDTO.requests.update.UpdatePostRequestDTO;
import tancredidangelo.capstone.entities.post.postDTO.responses.*;
import tancredidangelo.capstone.exceptions.BannedAccountException;
import tancredidangelo.capstone.helpers.ConverterPostDTO;
import tancredidangelo.capstone.entities.post.postSubclasses.writing.Writing;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.time.LocalDateTime;

import static tancredidangelo.capstone.helpers.ConverterPostDTO.convertToDTO;

@Service
@Slf4j

public class PostService {

    private final PostRepository postRepository;
    private final AccountService accountService;

    public PostService(PostRepository postRepository, AccountService accountService) {
        this.postRepository = postRepository;
        this.accountService = accountService;
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



    /// Get posts by account id
    public Page<Post> getPostsByAccountId(Long authorId, Pageable pageable) {
        if (this.accountService.findById(authorId).isBanned()) {
            throw new BannedAccountException("This account has been banned and is currently unavailable.");
        }
        return this.postRepository.findByAuthorIdOrderByTimestampDesc(authorId, pageable);
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