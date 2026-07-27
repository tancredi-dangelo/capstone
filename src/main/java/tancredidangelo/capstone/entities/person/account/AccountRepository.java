package tancredidangelo.capstone.entities.person.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIsBannedFalse(String username);

    Optional<Account> findByUsername(String username);

    // results STARTING BY typed characters are displayed first
    @Query("""
        SELECT a FROM Account a
        WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY
            CASE WHEN LOWER(a.username) LIKE LOWER(CONCAT(:query, '%')) THEN 0 ELSE 1 END,
            a.username ASC
    """)
    Page<Account> searchByUsernameMatching(@Param("query") String query, Pageable pageable);


}
