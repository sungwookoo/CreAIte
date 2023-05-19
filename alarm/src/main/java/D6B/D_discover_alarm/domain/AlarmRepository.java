package D6B.D_discover_alarm.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    List<Alarm> findByReceiverUid(String receiverUid);

    Optional<Alarm> findBySenderUidAndReceiverUidAndPictureId(String senderUid,String receiverUid,Long pictureId);
    List<Alarm> findByReceiverUidOrSenderUid(String receiverUid, String senderUid);

    List<Alarm> findByPictureId(Long pictureId);

}
