package D6B.D_discover_alarm.controller;

import D6B.D_discover_alarm.controller.dto.AlarmDto;
import D6B.D_discover_alarm.controller.dto.IsAliveDto;

import D6B.D_discover_alarm.controller.dto.NotificationDto;
import D6B.D_discover_alarm.service.AlarmService;
import D6B.D_discover_alarm.service.FirebaseCloudMessageService;
import D6B.D_discover_alarm.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final NotificationService notificationService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    public AlarmController(AlarmService alarmService,
                           NotificationService notificationService,
                           FirebaseCloudMessageService firebaseCloudMessageService) {

        this.alarmService = alarmService;
        this.notificationService = notificationService;
        this.firebaseCloudMessageService = firebaseCloudMessageService;
    }
    @GetMapping("/list/{user_uid}")
    public ResponseEntity<?> getAlarmList(@PathVariable String user_uid) {
        try {
            List<AlarmDto> dtos = alarmService.getAlarmList(user_uid);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @PostMapping("/create")
    public  ResponseEntity<Object> createNotification(@RequestBody NotificationDto notificationdto){
        NotificationDto createDto = notificationService.createNotification(notificationdto);
        try {
        firebaseCloudMessageService.sendMessageTo(notificationdto);
// 원래 코드
        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("sendMessageTo 오류");
        }
        return ResponseEntity.status(HttpStatus.OK).body(createDto);
    }

    @PutMapping("/{user_uid}/check")
    public ResponseEntity<String> Checked(@PathVariable String user_uid) {
        try {
            alarmService.checked(user_uid);
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    @PutMapping("/isalive")
    public ResponseEntity<String> isAlive(@RequestBody IsAliveDto isalivedto) {
        try {
            alarmService.isAlive(isalivedto);
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PutMapping("/marked")
    public ResponseEntity<String> marked(@RequestBody IsAliveDto isalivedto){
        try {
            alarmService.marked(isalivedto);
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("marked 에러 ");
        }
    }

    @PutMapping("/remove/{user_uid}")
    public ResponseEntity<String> remove(@PathVariable String user_uid){
        alarmService.remove(user_uid);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
    @DeleteMapping("/delete/{alarm_id}")
    public ResponseEntity<String> deleteAlarm(@PathVariable Long alarm_id) {
        alarmService.deleteAlarm(alarm_id);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
    @PutMapping("/picmove/{picture_id}")
    public ResponseEntity<String> picmove(@PathVariable Long picture_id){
        alarmService.picmove(picture_id);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
