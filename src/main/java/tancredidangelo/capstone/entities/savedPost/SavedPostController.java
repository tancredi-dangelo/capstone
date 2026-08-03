package tancredidangelo.capstone.entities.savedPost;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.requests.SavedPostRequestDTO;
import tancredidangelo.capstone.entities.savedPost.savedPostDTO.responses.SavedPostResponseDTO;

@RestController
@RequestMapping("/saved")
public class SavedPostController {

    /// dependency injection

    private final SavedPostService savedPostService;

    public SavedPostController(SavedPostService savedPostService) {
        this.savedPostService = savedPostService;
    }



    // methods


    /// ADD POST TO PERSONAL SAVED POST LIST
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public SavedPostResponseDTO savePost(SavedPostRequestDTO payload, Authentication authentication) {
        return this.savedPostService.save(payload, authentication);
    }



    /// REMOVE POST FROM LIST
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unsavePostById(Long id) {
        this.savedPostService.deleteById(id);
    }
    
}
