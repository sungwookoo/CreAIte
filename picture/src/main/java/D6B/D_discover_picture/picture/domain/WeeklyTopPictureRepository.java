package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyTopPictureRepository extends JpaRepository<WeeklyTopPicture, Long> {
    Optional<WeeklyTopPicture> findByPictureId(Long id);
}
