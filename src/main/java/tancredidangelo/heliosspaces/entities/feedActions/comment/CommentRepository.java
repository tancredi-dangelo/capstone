package tancredidangelo.heliosspaces.entities.feedActions.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /// find by post id (+ author)
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId")
    Page<Comment> findByPostIdWithAuthor(@Param("postId") Long postId, Pageable pageable);

    /// number of comments by post
    long countByPostId(Long postId);
}