package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyTopPictureRepository extends JpaRepository<MonthlyTopPicture, Long> {
    Optional<MonthlyTopPicture> findByPictureId(Long id);
}
