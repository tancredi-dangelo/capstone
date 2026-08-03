package tancredidangelo.capstone.entities.feedActions.comment;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.requests.CommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.requests.UpdateCommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.responses.CommentResponseDTO;

@RestController
@RequestMapping("/comments")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    // ---------- ENDPOINTS ----------------------------------------------------------------


    /// CREATE A NEW COMMENT ON A POST
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public CommentResponseDTO createComment(
            @RequestBody @Valid CommentRequestDTO payload,
            Authentication authentication
    ) {
        return this.commentService.save(payload, authentication);
    }



    /// GET ALL COMMENTS FOR A SPECIFIC POST (PAGINATED)
    @GetMapping("/post/{postId}")
    public Page<CommentResponseDTO> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return this.commentService.findByPostId(postId, pageable);
    }



    /// UPDATE COMMENT TEXT
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponseDTO updateComment(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCommentRequestDTO payload,
            Authentication authentication
    ) {
        return this.commentService.update(id, payload, authentication);
    }



    /// DELETE A COMMENT
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteComment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        this.commentService.deleteById(id, authentication);
    }


}