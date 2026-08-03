package tancredidangelo.capstone.entities.feedActions.follow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    Page<Follow> findByFollowedIdAndFollowStatus(Long followedId, FollowStatus status, Pageable pageable);

    Page<Follow> findByFollowerIdAndFollowStatus(Long followerId, FollowStatus status, Pageable pageable);

    long countByFollowedIdAndFollowStatus(Long followedId, FollowStatus status);

    long countByFollowerIdAndFollowStatus(Long followerId, FollowStatus status);
}
