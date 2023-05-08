package D6B.D_discover_picture.picture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByMakerUid(String makerUid);
    List<Picture> findAllByMakerUidAndIsPublic(String makerUid, Boolean isPublic);
}
