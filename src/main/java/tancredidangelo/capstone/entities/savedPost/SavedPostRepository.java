package tancredidangelo.capstone.entities.savedPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    @EntityGraph(attributePaths = {"post"})
    List<SavedPost> findByAccountId(Long id);

    boolean existsByAccountIdAndPostId(Long accountId, Long postId);
}
