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

    public static List<UserMadeDto> postAndBodyRequestToPicture(String uri, List<Long> pictureIds) {
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri(uri)
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

    public static void postAndBodyRequestToPicture(String url, DeleteUserHistoryInPicture deleteUserHistoryInPicture) {
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
