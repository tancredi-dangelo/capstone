package tancredidangelo.capstone.entities.savedPost;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.requests.SavedPostRequestDTO;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses.SavedPostResponseDTO;

import java.util.List;


@RestController
@RequestMapping("/saved")

public class SavedPostController {

    private final SavedPostService savedPostService;

    public SavedPostController(SavedPostService savedPostService) {
        this.savedPostService = savedPostService;
    }

    /// ADD POST TO PERSONAL SAVED POST LIST
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public SavedPostResponseDTO savePost(@RequestBody @Valid SavedPostRequestDTO payload, Authentication authentication) {
        return this.savedPostService.save(payload, authentication);
    }

    /// VIEW PERSONAL SAVED POSTS
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<SavedPostResponseDTO> getOwnSavedPosts(Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.savedPostService.findByAccountId(authenticatedAccount.getId());
    }

    /// REMOVE POST FROM LIST
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unsavePostById(@PathVariable Long id, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        this.savedPostService.deleteById(id, authenticatedAccount.getId());
    }


    /// CHECK IS POST SAVED BY AUTHENTICATED ACCOUNT
    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public boolean isPostSavedByAuthenticatedAccount(@RequestParam Long postId, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.savedPostService.isPostSavedByAuthenticatedAccount(postId, authenticatedAccount.getId());
    }
}