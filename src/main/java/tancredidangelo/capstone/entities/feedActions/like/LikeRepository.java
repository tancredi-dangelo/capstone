package tancredidangelo.capstone.entities.feedActions.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByAuthorIdAndPostId(Long authorId, Long postId);

    Optional<Like> findByAuthorIdAndCommentId(Long authorId, Long commentId);

    boolean existsByAuthorIdAndPostId(Long authorId, Long postId);

    boolean existsByAuthorIdAndCommentId(Long authorId, Long commentId);

    long countByPostId(Long postId);

    long countByCommentId(Long commentId);
}
