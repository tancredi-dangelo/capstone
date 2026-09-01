package tancredidangelo.capstone.entities.feedActions.like;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikeCommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikePostRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.response.LikeResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;

@RestController
@RequestMapping("/likes")
public class LikeController {

    /// dependency injection

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }


    // ---------------------- ENDPOINTS ------------------------------------------


    /// LIKE POST
    @PostMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public LikeResponseDTO likePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        return this.likeService.likePost(postId, authentication);
    }


    /// CHECK POST IS LIKED BY AUTHENTICATED ACCOUNT
    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    public boolean isPostLikedByAuthenticatedAccount(@RequestParam Long postId, Authentication authentication) {
        Account authenticatedAccount = (Account) authentication.getPrincipal();
        return this.likeService.isPostLikedByAuthenticatedAccount(postId, authenticatedAccount.getId());
    }


    /// UNLIKE POST
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unlikePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        this.likeService.unlikePost(postId, authentication);
    }


    /// GET LIKE COUNT -> POST
    @GetMapping("/posts/{postId}/count")
    public long getPostLikesCount(@PathVariable Long postId) {
        return this.likeService.getPostLikesCount(postId);
    }



    /// GET LIKE COUNT -> COMMENT
    @GetMapping("/comments/{commentId}/count")
    public long getCommentLikesCount(@PathVariable Long commentId) {
        return this.likeService.getCommentLikesCount(commentId);
    }
}