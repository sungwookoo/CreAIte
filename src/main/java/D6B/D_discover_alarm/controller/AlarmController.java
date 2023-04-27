package D6B.D_discover_alarm.controller;

import D6B.D_discover_alarm.controller.dto.AlarmDto;
import D6B.D_discover_alarm.controller.dto.NotificationDto;
import D6B.D_discover_alarm.service.AlarmService;
import D6B.D_discover_alarm.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.Notification;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final NotificationService notificationService;
    @Autowired
    public AlarmController(AlarmService alarmService,NotificationService notificationService) {
        this.alarmService = alarmService;
        this.notificationService = notificationService;
    }
    @GetMapping("/list/{user_id}")
    public ResponseEntity<List<AlarmDto>> getAlarmList(@PathVariable Long user_id){
            List<AlarmDto> dtos = alarmService.getAlarmList(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }


    @PostMapping("/create")
    public  ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationdto){
        NotificationDto createDto = notificationService.createNotification(notificationdto);
        return ResponseEntity.status(HttpStatus.OK).body(createDto);
    }

    @PutMapping("/{user_id}/check")
    public ResponseEntity<String> Checked(@PathVariable Long user_id){
        alarmService.checked(user_id);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
