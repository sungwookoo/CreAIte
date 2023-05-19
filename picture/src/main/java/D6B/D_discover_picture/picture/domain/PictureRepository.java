package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByMakerUidOrderByIdDesc(String makerUid);
    List<Picture> findAllByMakerUidAndIsAlive(String makerUid, Boolean isAlive);
    List<Picture> findAllByMakerUidAndIsPublicAndIsAliveOrderByIdDesc(String makerUid, Boolean isPublic, Boolean isAlive);

//    @Query(value = "SELECT * FROM picture " +
//            "WHERE is_public = true " +
//            "AND is_alive = true " +
//            "ORDER BY RAND() " +
//            "LIMIT 50", nativeQuery = true)
//    List<Picture> findRandPictures();

    List<Picture> findTop50ByIsPublicAndIsAliveOrderByIdDesc(boolean isPublic, boolean isAlive);

    List<Picture> findTop50ByIsPublicAndIsAliveAndCreatedAtBetweenOrderByLoveCountDesc(Boolean isPublic, Boolean isAlive, Instant start, Instant end);
}
