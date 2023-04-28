package D6B.D_discover_user.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoveRepository extends JpaRepository<Love, Long> {
    Optional<Love> findByUserUidAndPictureId(String userUid, Long pictureId);
    List<Love> findByUserId(Long userId);
    List<Love> findByUserUid(String uid);
    List<Love> findByPictureId(Long pictureId);
}
