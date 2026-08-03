package tancredidangelo.capstone.entities.savedPost;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.requests.SavedPostRequestDTO;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses.SavedPostResponseDTO;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;


@Service
@Slf4j
public class SavedPostService {

    /// dependency injection
    private final SavedPostRepository savedPostRepository;
    private final PostService postService;

    public SavedPostService(SavedPostRepository savedPostRepository, PostService postService) {
        this.savedPostRepository = savedPostRepository;
        this.postService = postService;
    }



    // methods

    /// SAVE POST
    @Transactional
    public SavedPostResponseDTO save(SavedPostRequestDTO payload, Authentication authentication) {

        Post post = this.postService.findById(payload.postId());
        Account authenticatedAccount = (Account) authentication.getPrincipal();

        if (this.savedPostRepository.existsByAccountIdAndPostId(authenticatedAccount.getId(), post.getId())) {
            throw new AlreadyExistsException("You already saved this post.");
        }

        SavedPost newSavedPost = new SavedPost(authenticatedAccount, post);

        SavedPost saved = this.savedPostRepository.save(newSavedPost);

        log.info("Post saved.");

        return SavedPostResponseDTO.fromEntity(saved);
    }


    /// FIND BY ID
    public SavedPost findById(Long id) {
        return this.savedPostRepository.findById(id).orElseThrow(() -> new NotFoundException("Post Not Found."));
    }


    /// GET SAVED POSTS LIST OF A SPECIFIC ACCOUNT
    public Page<SavedPostResponseDTO> findByAccountId(Long id, Pageable pageable) {
        Page<SavedPost> rawPosts = this.savedPostRepository.findByAccountId(id, pageable);
        return rawPosts.map(SavedPostResponseDTO::fromEntity);
    }


    /// DELETE
    @Transactional
    public void deleteById(Long id) {
        SavedPost found = findById(id);
        this.savedPostRepository.deleteById(found.getId());
    }
}
