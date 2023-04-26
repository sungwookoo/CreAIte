package D6B.D_discover_alarm.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm,Long> {
    List<Alarm> findByReceiverId(Long receiverId);

}
