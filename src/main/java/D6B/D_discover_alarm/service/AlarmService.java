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

    public AlarmService(AlarmRepository alarmRepository, ObjectMapper objectMapper) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * 해당 user의 모든 알림정보를 반환한다.
     * @param userUid ( 조회 하려는 유저)
     * @return
     */
    public List<AlarmDto> getAlarmList(Long userUid) {
        List<Alarm> alarms = alarmRepository.findByReceiverUid(userUid);
        log.info(alarms.toString());
        return alarms.stream()
                .filter(alarm -> !alarm.getIsRead() && alarm.getIsAlive())
                .map(alarm -> AlarmDto.from(alarm))
                .collect(Collectors.toList());
    }

    /**
     * 알림창 들어가면 알림 전부 읽음으로 표시되게
     * @param userUid
     */
    public void checked(Long userUid) {
        List<Alarm> alarms = alarmRepository.findByReceiverUid(userUid);
        alarms.forEach(alarm -> alarm.setIsRead(true));
        alarmRepository.saveAll(alarms);
    }
    @Transactional
    public void isAlive(IsAliveDto isalivedto) {
        Optional<Alarm> alarmOpt = alarmRepository.findBySenderUidAndReceiverUidAndPictureUid(
                isalivedto.getSenderUid(),
                isalivedto.getReceiverUid(),
                isalivedto.getPictureUid()
        );
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setIsAlive(false);
            alarm.setIsRead(false);
            alarmRepository.save(alarm);
        }
    }

    public void marked(IsAliveDto isalivedto) {
        Optional<Alarm> alarmOpt = alarmRepository.findBySenderUidAndReceiverUidAndPictureUid(
                isalivedto.getSenderUid(),
                isalivedto.getReceiverUid(),
                isalivedto.getPictureUid()
        );
        if (alarmOpt.isPresent()) {
            Alarm alarm = alarmOpt.get();
            alarm.setIsAlive(true);
            alarm.setIsRead(false);
            alarm.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant());
            alarmRepository.save(alarm);
        }
    }

    public void remove(Long userUid) {
        List<Alarm> alarms = alarmRepository.findByReceiverUidOrSenderUid(userUid,userUid);
        alarms.forEach(alarm -> alarm.setIsAlive(false));
        alarmRepository.saveAll(alarms);
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
