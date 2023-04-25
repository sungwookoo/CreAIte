package D6B.D_discover_user.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static D6B.D_discover_user.common.ConstValues.*;

@Slf4j
@Service
public class AuthorizeService {
    /**
     * token 정보 유효 확인 -> authorization : 인가
     * @param token
     * 정상적인 회원일 경우 : USER ID
     * 요청이 실패한 경우 : NON_MEMBER(-1)
     * 요청이 성공했으나, 회원이 아닌 경우 : UNAUTHORIZED_USER(-2)
     */
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

    /**
     * 해당 사용자가 회원인지 반환
     * @param status getAuthorization 함수의 반환값
     */
    public boolean isAuthorization(Long status) {
        if (status.equals(UNAUTHORIZED_USER) || status.equals(NON_MEMBER))
            return false;
        else
            return true;
    }
}
