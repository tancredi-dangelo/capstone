package tancredidangelo.capstone.entities.person.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    /// EXISTS BY USERNAME
    boolean existsByUsername(String username);


    /// FIND BY USERNAME
    Optional<Account> findByUsername(String username);


    /// FIND BY MATCH USERNAME results STARTING BY typed characters are displayed first, IS BANNED = FALSE
    @Query("""
        SELECT a FROM Account a
        WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :query, '%'))
        AND a.isBanned = FALSE
        ORDER BY
            CASE WHEN LOWER(a.username) LIKE LOWER(CONCAT(:query, '%')) THEN 0 ELSE 1 END,
            a.username ASC
    """)
    Page<Account> searchByUsernameMatchingAndIsBannedFalse(@Param("query") String query, Pageable pageable);




}
