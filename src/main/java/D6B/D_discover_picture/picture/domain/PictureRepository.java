package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByMakerUid(String makerUid);
    List<Picture> findAllByMakerUidAndIsPublic(String makerUid, Boolean isPublic);

    @Query(value = "SELECT * FROM picture " +
            "WHERE is_public = true " +
            "AND is_alive = true " +
            "AND created_at >= :createdAt " +
            "ORDER BY RAND() " +
            "LIMIT 50", nativeQuery = true)
    List<Picture> findAllByCreatedAtAfter(@Param(value = "createdAt") Instant createdAt);

    List<Picture> findTop50ByIsPublicAndIsAliveAndCreatedAtBetweenOrderByLoveCountDesc(Boolean isPublic, Boolean isAlive, Instant start, Instant end);
}
