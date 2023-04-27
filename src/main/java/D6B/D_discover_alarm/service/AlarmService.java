package D6B.D_discover_alarm.service;

import D6B.D_discover_alarm.controller.dto.AlarmDto;
import D6B.D_discover_alarm.domain.Alarm;
import D6B.D_discover_alarm.domain.AlarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
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
     * @param userId ( 조회 하려는 유저)
     * @return
     */
    public List<AlarmDto> getAlarmList(Long userId) {
        List<Alarm> alarms = alarmRepository.findByReceiverId(userId);
        log.info(alarms.toString());
        return alarms.stream()
                .map(alarm -> AlarmDto.from(alarm))
                .collect(Collectors.toList());
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
