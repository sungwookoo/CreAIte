package D6B.D_discover_user.user.service.msa;

import D6B.D_discover_user.user.controller.dto.UserPicsResponseDto;
import D6B.D_discover_user.user.service.dto.DeleteUserHistoryInPicture;
import D6B.D_discover_user.user.service.dto.UserMadeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static D6B.D_discover_user.common.ConstValues.PICTURE_SERVER_CLIENT;

@Service
@Slf4j
public class PictureCallService {
    /**
     * 함수명 : (요청구분) + Request + (요청에 추가로 들어가는 값들) + (어느 서비스에 요청하는지) + (Void)
     */

    public static String postRequestToPicture(String url) {
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * Picture 서비스에 post 요청을 보냄
     * @param url : picture/*** -> 여기 ***에 들어갈 url
     */
    public static void postRequestToPictureThenVoid(String url) {
        try {
            PICTURE_SERVER_CLIENT.post()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public static List<UserMadeDto> postRequestWithBodyToPicture(String url, List<Long> pictureIds) {
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri(url)
                    .body(BodyInserters.fromValue(pictureIds))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserMadeDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    public static void postRequestWithBodyToPicture(String url, DeleteUserHistoryInPicture deleteUserHistoryInPicture) {
        try {
            PICTURE_SERVER_CLIENT.post()
                    .uri(url)
                    .body(BodyInserters.fromValue(deleteUserHistoryInPicture))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(void.class)
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    public static List<UserMadeDto> getRequestToPicture(String url) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserMadeDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }
}
