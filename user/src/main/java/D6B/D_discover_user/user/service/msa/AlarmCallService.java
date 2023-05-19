package D6B.D_discover_user.user.service.msa;

import D6B.D_discover_user.user.service.dto.ActivateAlarmRequestDto;
import D6B.D_discover_user.user.service.dto.DeactivateAlarmRequestDto;
import D6B.D_discover_user.user.service.dto.PostAlarmRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static D6B.D_discover_user.common.ConstValues.ALARM_SERVER_CLIENT;

@Slf4j
public class AlarmCallService {
    /**
     * 함수명 : (요청구분) + Request + (요청에 추가로 들어가는 값들) + (어느 서비스에 요청하는지) + (Void)
     */

    public static void postRequestWithBodyToAlarmThenVoid(String url, PostAlarmRequestDto postAlarmRequestDto) {
        try {
            ALARM_SERVER_CLIENT.post()
                    .uri(url)
                    .body(BodyInserters.fromValue(postAlarmRequestDto))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public static void putRequestToAlarmThenVoid(String url) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public static void putRequestWithBodyToAlarmThenVod(String url, DeactivateAlarmRequestDto deactivateAlarmRequestDto) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri(url)
                    .body(BodyInserters.fromValue(deactivateAlarmRequestDto))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public static void putRequestWithHeaderAndBodyToAlarmThenVoid(String url, ActivateAlarmRequestDto activateAlarmRequestDto) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(BodyInserters.fromValue(activateAlarmRequestDto))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }
}
