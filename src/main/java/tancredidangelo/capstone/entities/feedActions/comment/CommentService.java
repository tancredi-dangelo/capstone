package tancredidangelo.capstone.entities.feedActions.comment;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.requests.CommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.requests.UpdateCommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.comment.commentDTO.responses.CommentResponseDTO;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.exceptions.NotFoundException;
import tancredidangelo.capstone.exceptions.UnauthorizedException;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    /// CREATE COMMENT
    @Transactional
    public CommentResponseDTO save(CommentRequestDTO payload, Authentication authentication) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Post post = this.postService.findById(payload.postId());

        Comment newComment = new Comment(authenticatedAccount, post, payload.text().trim());
        Comment savedComment = this.commentRepository.save(newComment);

        log.info("Comment created.");

        return CommentResponseDTO.fromEntity(savedComment);
    }

    /// FIND ENTITY BY ID
    public Comment findById(Long id) {
        return this.commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with ID " + id + " not found."));
    }

    /// GET COMMENTS BY POST ID (PAGINATED)
    public Page<CommentResponseDTO> findByPostId(Long postId, Pageable pageable) {

        this.postService.findById(postId);

        Page<Comment> comments = this.commentRepository.findByPostIdWithAuthor(postId, pageable);
        return comments.map(CommentResponseDTO::fromEntity);
    }

    /// UPDATE COMMENT TEXT
    @Transactional
    public CommentResponseDTO update(Long commentId, UpdateCommentRequestDTO payload, Authentication authentication) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Comment comment = findById(commentId);

        if (!comment.getAuthor().getId().equals(authenticatedAccount.getId())) {
            throw new UnauthorizedException("You are not authorized to edit this comment.");
        }

        comment.setText(payload.text());
        Comment updated = this.commentRepository.save(comment);

        log.info("Comment updated");

        return CommentResponseDTO.fromEntity(updated);
    }

    /// DELETE COMMENT
    @Transactional
    public void deleteById(Long commentId, Authentication authentication) {

        Account authenticatedAccount = (Account) authentication.getPrincipal();
        Comment comment = findById(commentId);

        // comment can be deleted by author or post Owner
        boolean isCommentAuthor = comment.getAuthor().getId().equals(authenticatedAccount.getId());
        boolean isPostAuthor = comment.getPost().getAuthor().getId().equals(authenticatedAccount.getId());

        if (!isCommentAuthor && !isPostAuthor) {
            throw new UnauthorizedException("You are not authorized to delete this comment.");
        }

        this.commentRepository.delete(comment);
        log.info("Comment deleted");
    }
}

