package tancredidangelo.capstone.entities.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.postDTO.PostResponseDTO;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/home")
public class PostController {

    /// dependency injection
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }



    // -------------------- ENDPOINTS ------------------------------------------------------

    /// FEED -> GET "[...](http://localhost/home)" 200 OK
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<PostResponseDTO> getFeed(Authentication authentication, Pageable pageable) {
        Long accountId = ((Account) Objects.requireNonNull(authentication.getPrincipal())).getId();
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return this.postService.getFeed(accountId, since, pageable);
    }


    /// SINGLE POST -> GET "[...](http://localhost/posts/{id})" 200 OK
    @GetMapping("/posts/{id}")
    public PostResponseDTO getPostById(@PathVariable Long id) {
        Post found = postService.findById(id);
        return new PostResponseDTO(found.getAuthor(), found.getCaption(), found.getTimestamp(), found.getLikes(), found.getComments(), found.isUpdated());
    }



    /// DELETE POST
    @DeleteMapping("/posts/{id}")
    @PreAuthorize("@authConfig.isPostOwnerOrAdmin(#id, authentication)")
    public void deletePost(@PathVariable Long id) {
        postService.deleteById(id);
    }




    // GET PUBLIC OWN ACCOUNT OR PUBLIC USER ACCOUNT
    // ** ENDPOINT IN ACCOUNT-CONTROLLER **

}
