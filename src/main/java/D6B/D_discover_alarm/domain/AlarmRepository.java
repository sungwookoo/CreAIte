package D6B.D_discover_alarm.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    List<Alarm> findByReceiverUid(Long receiverUid);

    Optional<Alarm> findBySenderUidAndReceiverUidAndPictureId(Long senderUid,Long receiverUid,Long pictureId);
    List<Alarm> findByReceiverUidOrSenderUid(Long receiverUid, Long senderUid);

    List<Alarm> findByPictureId(Long pictureId);

}
