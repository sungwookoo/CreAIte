package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.service.dto.LoveCheckAndMakerResponse;
import D6B.D_discover_picture.picture.service.dto.PictureLoveCheckRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static D6B.D_discover_picture.common.ConstValues.*;

@Slf4j
@Service
public class MsaService {

    /** 여기는 Picture -> User **/

    // 좋아요 여부와 제작자 닉네임 받아오는 요청
    public static List<LoveCheckAndMakerResponse> checkLoveAndGetName(List<PictureLoveCheckRequest> list) {
        try {
            return USER_SERVER_CLIENT.post()
                    .uri("/find_love_check_maker_name")
                    .body(BodyInserters.fromValue(list))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<LoveCheckAndMakerResponse>>() {
                    })
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 제작자 닉네임 받아오는 요청
    public static List<String> checkMakerName(List<String> list) {
        try {
            return USER_SERVER_CLIENT.post()
                    .uri("/find_maker_name")
                    .body(BodyInserters.fromValue(list))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    // 특정 그림에 달린 좋아요 삭제 요청
    public static void deleteLikeRequest(Long pictureId) {
        try {
            USER_SERVER_CLIENT.post()
                    .uri("/like/delete/" + pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /** 여기는 Picture -> Alarm **/

    // 특정 그림의 좋아요 알림 삭제 요청
    public static void deleteLikeAlarmRequest(Long pictureId) {
        try {
            ALARM_SERVER_CLIENT.put()
                    .uri("/picmove/" + pictureId)    /// uri 협의 필요
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
