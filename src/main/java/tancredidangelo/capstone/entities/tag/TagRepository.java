package tancredidangelo.capstone.entities.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Tag> findByTitleContainingIgnoreCase(String name);

}
