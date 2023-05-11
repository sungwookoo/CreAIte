package D6B.D_discover_user.user.service.msa;

import D6B.D_discover_user.user.controller.dto.UserPicsResponseDto;
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


    // List<UserPicResponseDto>, post, body
    public static List<UserPicsResponseDto> getLikePictureInfo(String uri, List<Long> pictureIds) {
        try {
            return PICTURE_SERVER_CLIENT.post()
                    .uri(uri)
                    .body(BodyInserters.fromValue(pictureIds))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(new ParameterizedTypeReference<List<UserPicsResponseDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    // List<UserPicsResponseDto>, get
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
}
