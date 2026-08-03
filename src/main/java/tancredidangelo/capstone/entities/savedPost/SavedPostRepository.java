package tancredidangelo.capstone.entities.savedPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    Page<SavedPost> findByAccountId(Long id, Pageable pageable);

    boolean existsByAccountIdAndPostId(Long accountId, Long postId);
}
