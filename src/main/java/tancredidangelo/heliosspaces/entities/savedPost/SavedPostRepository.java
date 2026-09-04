package tancredidangelo.heliosspaces.entities.savedPost;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    @EntityGraph(attributePaths = {"post"})
    List<SavedPost> findByAccountId(Long id);

    Optional<SavedPost> findByPostId(Long postId);

    boolean existsByAccountIdAndPostId(Long accountId, Long postId);
}
