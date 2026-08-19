package tancredidangelo.capstone.entities.savedPost;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.requests.SavedPostRequestDTO;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses.SavedPostResponseDTO;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

@Service
@Slf4j
public class SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final PostService postService;
    private final AccountService accountService;

    public SavedPostService(SavedPostRepository savedPostRepository, PostService postService, AccountService accountService) {
        this.savedPostRepository = savedPostRepository;
        this.postService = postService;
        this.accountService = accountService;
    }

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

        log.info("Post with ID {} saved for account ID {}.", post.getId(), authenticatedAccount.getId());

        return SavedPostResponseDTO.fromEntity(saved);
    }

    /// FIND BY ID
    public SavedPost findById(Long id) {
        return this.savedPostRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Saved post with ID " + id + " not found."));
    }

    /// IS POST SAVED BY AUTHENTICATED ACCOUNT
    public boolean isPostSavedByAuthenticatedAccount(Long postId, Long accountId) {
        Account managedAccount = this.accountService.findById(accountId);
        return this.savedPostRepository.existsByAccountIdAndPostId(managedAccount.getId(), postId);
    }

    /// GET SAVED POSTS LIST OF A SPECIFIC ACCOUNT
    public Page<SavedPostResponseDTO> findByAccountId(Long id, Pageable pageable) {
        Page<SavedPost> rawPosts = this.savedPostRepository.findByAccountId(id, pageable);
        return rawPosts.map(SavedPostResponseDTO::fromEntity);
    }

    /// DELETE BY SAVED_POST ID
    @Transactional
    public void deleteById(Long id, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        SavedPost found = findById(id);

        if (!found.getAccount().getId().equals(authenticatedAccount.getId())) {
            throw new UnauthorizedException("You don't have authorization to perform this action.");
        }

        this.savedPostRepository.delete(found);
    }
}