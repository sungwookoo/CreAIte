package D6B.D_discover_alarm.service;

import D6B.D_discover_alarm.controller.dto.NotificationDto;
import D6B.D_discover_alarm.domain.Alarm;
import D6B.D_discover_alarm.domain.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private AlarmRepository alarmRepository;

    @Transactional
    public NotificationDto createNotification(NotificationDto dto) {
        Alarm alarm = Alarm.createAlarm(dto);
        Alarm created = alarmRepository.save(alarm);
        return NotificationDto.createNotificationDto(created);
    }
}
