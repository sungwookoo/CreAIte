package D6B.D_discover_picture.common.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static D6B.D_discover_picture.common.ConstValues.*;

@Slf4j
@Service
public class AuthorizeService {

    public Long getAuthorization(String token) {
        Long userId = NON_MEMBER;
        try {
            userId = AUTH_SERVER_CLIENT.get()
                    .uri(AUTH_URI)
                    .header("Authorization", token)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Long.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
            userId = UNAUTHORIZED_USER;
        }
        return userId;
    }

    public boolean isAuthorization(Long status) {
        if (status.equals(UNAUTHORIZED_USER) || status.equals(NON_MEMBER))
            return false;
        else
            return true;
    }

}
