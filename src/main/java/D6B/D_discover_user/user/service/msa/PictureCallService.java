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
     * 좋아요 그림의 정보를 가져오는 WebClient 코드
     */
    public static List<UserMadeDto> getLikePictureInfo(String uri, List<Long> pictureIds) {
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


    /**
     * 만든 그림의 정보를 가져오는 WebClient 코드
     */
    public static List<UserMadeDto> getMadePictureInfo(String uri) {
        try {
            return PICTURE_SERVER_CLIENT.get()
                    .uri(uri)
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

    public static void deactivatePictureAndMinusLoveWhenDeleteUser(String url, DeleteUserHistoryInPicture deleteUserHistoryInPicture) {
        try {
            PICTURE_SERVER_CLIENT.post()
                    .uri(url)// 여기 바뀔예정
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

    //*****************body 없는 것*********************//
    public static String getPictureUrlAndPlusLoveWhenFirstLove(String url) {
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

    public static void plusLoveCountWhenLoveActivate(String url) {
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

    public static void minusLoveCountWhenLoveDeactivate(String url) {
        try {
            PICTURE_SERVER_CLIENT.post()
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
}
