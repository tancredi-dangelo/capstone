package tancredidangelo.capstone.entities.feedActions.like;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikeCommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikePostRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.response.LikeResponseDTO;

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



    /// LIKE COMMENT
    @PostMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public LikeResponseDTO likeComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        return this.likeService.likeComment(commentId, authentication);
    }



    /// UNLIKE COMMENT
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unlikeComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        this.likeService.unlikeComment(commentId, authentication);
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