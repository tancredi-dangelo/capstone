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
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.post.Post;
import tancredidangelo.capstone.entities.post.PostService;
import tancredidangelo.capstone.exceptions.AlreadyExistsException;
import tancredidangelo.capstone.exceptions.NotFoundException;

@Service
@Slf4j
public class LikeService {

    /// dependency injection

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;

    public LikeService(LikeRepository likeRepository, PostService postService, CommentService commentService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
        this.commentService = commentService;
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

        log.info("Post ID {} liked by Account ID {}.", postId, author.getId());
        Like saved = this.likeRepository.save(like);

        return LikeResponseDTO.fromEntity(saved);
    }




    /// LIKE COMMENT
    @Transactional
    public LikeResponseDTO likeComment(Long commentId, Authentication authentication) {
        Account author = (Account) authentication.getPrincipal();

        if (this.likeRepository.existsByAuthorIdAndCommentId(author.getId(), commentId)) {
            throw new AlreadyExistsException("You have already liked this comment.");
        }

        Comment comment = this.commentService.findById(commentId);
        Like like = new Like(author, comment);

        log.info("Comment ID {} liked by Account ID {}.", commentId, author.getId());
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

        log.info("Post ID {} unliked by Account ID {}.", postId, author.getId());

    }



    /// UNLIKE COMMENT
    @Transactional
    public void unlikeComment(Long commentId, Authentication authentication) {
        Account author = (Account) authentication.getPrincipal();

        Like like = this.likeRepository.findByAuthorIdAndCommentId(author.getId(), commentId)
                .orElseThrow(() -> new NotFoundException("Like not found on this comment."));

        this.likeRepository.delete(like);

        log.info("Comment ID {} unliked by Account ID {}.", commentId, author.getId());
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