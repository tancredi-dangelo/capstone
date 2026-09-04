package tancredidangelo.heliosspaces.entities.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/// UNIQUE REPOSITORY FOR ALL POST SUBCLASSES

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {


    // ----------------- PUBLIC METHODS --------------------------------------------------------------

    @Query("""
    SELECT p FROM Post p
    WHERE (
        p.author.id = :accountId
        OR p.author.id IN (
            SELECT f.followed.id FROM Follow f
            WHERE f.follower.id = :accountId
              AND f.followStatus = "ACCEPTED"
              AND f.followed.isBanned = FALSE
        )
    )
    AND p.timestamp >= :since
    ORDER BY p.timestamp DESC
    """)
    List<Post> findFeedIncludingOwnPosts(
            @Param("accountId") Long accountId,
            @Param("since") LocalDateTime since
    );

    /// FIND POST BY ACCOUNT AND ORDER BY DATE (ACCOUNT PAGE)
    List<Post> findByAuthorIdOrderByTimestampDesc(Long authorId);



}
