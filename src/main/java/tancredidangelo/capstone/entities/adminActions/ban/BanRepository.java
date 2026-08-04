package tancredidangelo.capstone.entities.adminActions.ban;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {

    Page<Ban> findByAccountId(Long accountId, Pageable pageable);

}