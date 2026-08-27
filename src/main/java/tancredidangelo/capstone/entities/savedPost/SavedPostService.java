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
import tancredidangelo.capstone.exceptions.ForbiddenException;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

import java.util.List;

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

    /// FIND BY POST ID
    public SavedPost findByPostId(Long postId) {
        return this.savedPostRepository.findByPostId(postId).orElseThrow(() -> new NotFoundException("Saved post with PostID " + postId + " not found."));
    }

    /// IS POST SAVED BY AUTHENTICATED ACCOUNT
    public boolean isPostSavedByAuthenticatedAccount(Long postId, Long accountId) {
        Account managedAccount = this.accountService.findById(accountId);
        return this.savedPostRepository.existsByAccountIdAndPostId(managedAccount.getId(), postId);
    }

    /// GET SAVED POSTS LIST OF A SPECIFIC ACCOUNT
    public List<SavedPostResponseDTO> findByAccountId(Long id) {
        Account managedAccount = this.accountService.findById(id);
        List<SavedPost> rawPosts = this.savedPostRepository.findByAccountId(managedAccount.getId());
        return rawPosts.stream().map(SavedPostResponseDTO::fromEntity).toList();
    }

    /// DELETE BY SAVED_POST ID
    @Transactional
    public void deleteById(Long postId, Long authenticatedAccountId) {
        Account managedAccount = this.accountService.findById(authenticatedAccountId);

        SavedPost found = findByPostId(postId);

        if (!found.getAccount().getId().equals(managedAccount.getId())) {
            throw new ForbiddenException("You don't have authorization to perform this action.");
        }

        this.savedPostRepository.delete(found);
    }
}