package tancredidangelo.capstone.entities.person.account.stack;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    /// EXISTS BY USERNAME
    boolean existsByUsername(String username);


    /// FIND BY USERNAME
    Optional<Account> findByUsername(String username);


    /// FIND BY USER ID
    List<Account> findByUserId(UUID userId);



}
