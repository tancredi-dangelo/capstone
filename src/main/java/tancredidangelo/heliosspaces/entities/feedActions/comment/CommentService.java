package tancredidangelo.heliosspaces.entities.feedActions.comment;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.heliosspaces.entities.feedActions.comment.commentDTO.requests.CommentRequestDTO;
import tancredidangelo.heliosspaces.entities.feedActions.comment.commentDTO.requests.UpdateCommentRequestDTO;
import tancredidangelo.heliosspaces.entities.feedActions.comment.commentDTO.responses.CommentResponseDTO;
import tancredidangelo.heliosspaces.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.heliosspaces.entities.feedActions.notification.NotificationService;
import tancredidangelo.heliosspaces.entities.feedActions.notification.NotificationType;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.account.stack.AccountService;
import tancredidangelo.heliosspaces.entities.post.Post;
import tancredidangelo.heliosspaces.entities.post.PostService;
import tancredidangelo.heliosspaces.exceptions.NotFoundException;
import tancredidangelo.heliosspaces.exceptions.UnauthorizedException;

@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository, PostService postService, AccountService accountService, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    /// CREATE COMMENT
    @Transactional
    public CommentResponseDTO createComment(Long authorId, Long postId, CommentRequestDTO payload) {

        Account author = this.accountService.findById(authorId);
        Post post = this.postService.findById(postId);

        Comment newComment = new Comment(author, post, payload.text().trim());
        Comment savedComment = this.commentRepository.save(newComment);

        if (!authorId.equals(post.getAuthor().getId())) {
            NotificationRequestDTO newNotification = new NotificationRequestDTO(
                    NotificationType.COMMENT_TO_POST,
                    author.getId(),
                    post.getAuthor().getId(),
                    post.getId(),
                    null);

            this.notificationService.createNotification(newNotification);
        }


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
    }
}

