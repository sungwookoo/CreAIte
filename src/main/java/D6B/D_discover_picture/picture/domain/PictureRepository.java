package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByMakerUid(String makerUid);
    List<Picture> findAllByMakerUidAndIsPublic(String makerUid, Boolean isPublic);

    @Query(value = "SELECT * FROM Picture order by RAND() limit 50", nativeQuery = true)
    List<Picture> findByIsPublicAndIsAliveAndCreatedAtAfter(Boolean isPublic, Boolean isAlive, Instant createdAt);
}
