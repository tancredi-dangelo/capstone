package tancredidangelo.capstone.entities.feedActions.like;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tancredidangelo.capstone.entities.feedActions.comment.Comment;
import tancredidangelo.capstone.entities.feedActions.comment.CommentService;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikeCommentRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.request.LikePostRequestDTO;
import tancredidangelo.capstone.entities.feedActions.like.likeDTO.response.LikeResponseDTO;
import tancredidangelo.capstone.entities.feedActions.notification.Notification;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationRequestDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationDTO.NotificationResponseDTO;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationService;
import tancredidangelo.capstone.entities.feedActions.notification.NotificationType;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.account.stack.AccountService;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;

import java.util.List;

@Service
@Slf4j
public class LikeService {

    /// dependency injection

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository, PostService postService, CommentService commentService, AccountService accountService, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    // ------------------- METHODS ---------------------------------------------

    /// LIKE POST
    @Transactional
    public LikeResponseDTO likePost(Long postId, Authentication authentication) {

        Account author = (Account) authentication.getPrincipal();

        if (this.likeRepository.existsByAuthorIdAndPostId(author.getId(), postId)) {
            throw new AlreadyExistsException("You have already liked this post.");
        }

        Post post = this.postService.findById(postId);
        Like like = new Like(author, post);

        if (!author.getId().equals(post.getAuthor().getId())) {

            NotificationRequestDTO newNotification = new NotificationRequestDTO(
                    NotificationType.LIKE_TO_POST,
                    author.getId(),
                    post.getAuthor().getId(),
                    post.getId(),
                    null);

            this.notificationService.createNotification(newNotification);
        }

        Like saved = this.likeRepository.save(like);

        return LikeResponseDTO.fromEntity(saved);
    }




    /// UNLIKE POST
    @Transactional
    public void unlikePost(Long postId, Authentication authentication) {

        Account author = (Account) authentication.getPrincipal();

        Like like = this.likeRepository.findByAuthorIdAndPostId(author.getId(), postId)
                .orElseThrow(() -> new NotFoundException("Like not found on this post."));

        this.likeRepository.delete(like);

    }


    /// CHECK POST IS LIKED BY AUTHENTICATED ACCOUNT
    public boolean isPostLikedByAuthenticatedAccount(Long postId, Long accountId) {
        Account managedAccount = this.accountService.findById(accountId);
        return this.likeRepository.existsByAuthorIdAndPostId(managedAccount.getId(), postId);
    }




    /// GET LIKES COUNT
    public long getPostLikesCount(Long postId) {
        return this.likeRepository.countByPostId(postId);
    }



    /// GET COMMENT COUNT
    public long getCommentLikesCount(Long commentId) {
        return this.likeRepository.countByCommentId(commentId);
    }
}