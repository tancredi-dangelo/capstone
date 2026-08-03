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
    public Page<SavedPostResponseDTO> getOwnSavedPosts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return this.savedPostService.findByAccountId(authenticatedAccount.getId(), pageable);
    }

    /// REMOVE POST FROM LIST
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unsavePostById(@PathVariable Long id, Authentication authentication) {
        this.savedPostService.deleteById(id, authentication);
    }
}