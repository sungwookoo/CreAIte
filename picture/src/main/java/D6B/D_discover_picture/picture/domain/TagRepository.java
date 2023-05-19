package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByWord(String word);
    List<Tag> findByTagCountGreaterThanEqual(Long count);

}
