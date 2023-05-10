package D6B.D_discover_alarm.service;

import D6B.D_discover_alarm.controller.dto.AlarmDto;
import D6B.D_discover_alarm.controller.dto.IsAliveDto;
import D6B.D_discover_alarm.domain.Alarm;
import D6B.D_discover_alarm.domain.AlarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public AlarmService(AlarmRepository alarmRepository, ObjectMapper objectMapper, FirebaseCloudMessageService firebaseCloudMessageService) {
        this.alarmRepository = alarmRepository;
        this.firebaseCloudMessageService = firebaseCloudMessageService;
    }

    /**
     * 해당 user의 모든 알림정보를 반환한다.
     * @param userUid ( 조회 하려는 유저)
     * @return
     */
    public List<AlarmDto> getAlarmList(String userUid) {
        try {
            List<Alarm> alarms = alarmRepository.findByReceiverUid(userUid);
            log.info(alarms.toString());
            return alarms.stream()
                    .filter(alarm -> !alarm.getIsRead() && alarm.getIsAlive())
                    .map(alarm -> AlarmDto.from(alarm))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get the alarm list");
        }
    }

    /**
     * 알림창 들어가면 알림 전부 읽음으로 표시되게
     * @param userUid
     */
    public void checked(String userUid) {
        try {
            List<Alarm> alarms = alarmRepository.findByReceiverUid(userUid);
            alarms.forEach(alarm -> alarm.setIsRead(true));
            alarmRepository.saveAll(alarms);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check the alarms");
        }
    }
    @Transactional
    public void isAlive(IsAliveDto isalivedto) {
        try {
            Optional<Alarm> alarmOpt = alarmRepository.findBySenderUidAndReceiverUidAndPictureId(
                    isalivedto.getSenderUid(),
                    isalivedto.getReceiverUid(),
                    isalivedto.getPictureId()
            );
            if (alarmOpt.isPresent()) {
                Alarm alarm = alarmOpt.get();
                alarm.setIsAlive(false);
                alarm.setIsRead(false);
                alarmRepository.save(alarm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update the alarm status");
        }
    }

    public void marked(IsAliveDto isalivedto) {
        Optional<Alarm> alarmOpt = alarmRepository.findBySenderUidAndReceiverUidAndPictureId(
                isalivedto.getSenderUid(),
                isalivedto.getReceiverUid(),
                isalivedto.getPictureId()
        );
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setIsAlive(true);
            alarm.setIsRead(false);
            alarm.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
            alarmRepository.save(alarm);
//            String fcmToken = "fEvCxJFWSjWZyfsqiqMFJl:APA91bGr7IPRVsNnTgNTm9IE4UEUbIdGApDci77uTPYRrQpAfMGD6QyDeqQRf0aPHfenMYvd9dJOQwQiaHfmSDyO-05aqUOTPxxXSe1LSBy8f1cpdjVVE_ZfUPqrvFmyWBc8N5UqvT49";
            String fcmToken = firebaseCloudMessageService.getFCMTokenByUserId(alarm.getReceiverUid());

            String SenderName = alarm.getSenderName();
            // contentType 설정
            String msg = SenderName + "님이 좋아요를 누르셨습니다.";
            // overloading 함수 재사용
            //sendMessageTo(fcmToken, "Creaite", msg, https://i.imgur.com/WUtsRM9.png);
            //firebaseCloudMessageService.sendMessageTo(fcmToken, "Creaite", msg, alarm.getSenderImgSrc());
            try {
                firebaseCloudMessageService.sendMessageTo(fcmToken, "Creaite", msg, alarm.getSenderImgSrc());
            } catch (IOException e) {
                e.printStackTrace();
                // 여기에서 예외 처리를 수행하거나 필요에 따라 반환 타입을 변경하여 실패 응답을 처리할 수 있습니다.
            }
        }
    }

    public void remove(String userUid) {
        List<Alarm> alarms = alarmRepository.findByReceiverUidOrSenderUid(userUid,userUid);
        alarms.forEach(alarm -> {alarm.setIsAlive(false);
                                alarm.setContent("삭제된 알람입니다");});
        alarmRepository.saveAll(alarms);
    }

    public void picmove(Long picture_id) {
        List<Alarm> alarms = alarmRepository.findByPictureId(picture_id);
        alarms.forEach(alarm -> {alarm.setIsAlive(false);
                                alarm.setContent("삭제된 알람입니다");});
        alarmRepository.saveAll(alarms);
    }

    @Transactional
    public void deleteAlarm(Long alarmId) {
        Optional<Alarm> alarmOptional =  alarmRepository.findById(alarmId);
        if (alarmOptional.isPresent()) {
            Alarm alarm = alarmOptional.get();
            alarm.setIsAlive(false);
            alarmRepository.save(alarm);
        }
    }

    /**
     * user 이름 가져오기
     * @param userId
     * @return
     */
//    public String getNameByUserId(Long userId) {
//        try {
//            return USER_SERVER_CLIENT.get()
//                    .uri("/user/" + userId.toString() + "/nickname")
//                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(Client4xxException::new))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(Client5xxException::new))
//                    .bodyToMono(String.class)
//                    .block();
//        } catch (Exception e) {
//            return "";
//        }
//    }

    /**
     * 유저 이미지 가져오기
     * @param userId
     * @return
     */
//    public String getUserImgSrc(Long userId) {
//        try {
//            return USER_SERVER_CLIENT.get()
//                    .uri("/user/img/" + userId.toString())
//                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(Client4xxException::new))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(Client5xxException::new))
//                    .bodyToMono(String.class)
//                    .block();
//        } catch (Exception e) {
//            return "";
//        }
//    }

    /**
     * 사진 이미지 가져오기
     * @param pictureId
     * @return
     */
//    public String getPictureImgSrc(Long pictureId) {
//        try {
//            return USER_SERVER_CLIENT.get()
//                    .uri("/picture/img/" + pictureId.toString())
//                    .retrieve()
//                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(Client4xxException::new))
//                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(Client5xxException::new))
//                    .bodyToMono(String.class)
//                    .block();
//        } catch (Exception e) {
//            return "";
//        }
//    }
}
