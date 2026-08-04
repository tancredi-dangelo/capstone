package tancredidangelo.capstone.entities.adminActions.flag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {

    Page<Flag> findByUserId(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);
}