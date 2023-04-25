package D6B.D_discover_user.user.controller;

import D6B.D_discover_user.common.service.AuthorizeService;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

import static D6B.D_discover_user.common.ConstValues.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthorizeService authorizeService;

    @Autowired
    public UserController(UserService userService, AuthorizeService authorizeService) {
        this.userService = userService;
        this.authorizeService = authorizeService;
    }

    @GetMapping("")
    public ResponseEntity<UserExistInfoDto> isMember(@RequestHeader("Authorization") String token) {
        final Long userId = authorizeService.getAuthorization(token);
        if(authorizeService.isAuthorization(userId))
            return ReponseEntity.ok(UserExistInfoDto.from(userId));
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/activate/{uid}")
    public ResponseEntity<Boolean> isActive(@PathVariable Long uid) {
        try {
            User userInformation = userService.getUserInformation(uid);
            if(userInformation.getActivate())
                return ResponseEntity.ok(true);
            else
                return ResponseEntity.ok(false);
        } catch (UserNotFoundException e) {
            log.error("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("")
    public ResponseEntity<Object> enrollUserInformation(@RequestHeader("Authorization") String token,
                                                        @RequestBody UserRequestDto userRequestDto) {
        final Long userId = authorizeService.getAuthorization(token);
        if(userId.equals(UNAUTHORIZED_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(
                UserExistInfoDto.from(UserService.enrollUser(UserRequestDto.to(userRequestDto, userId), token)));
    }

    /
    @GetMapping("/love")
    public void readUserLoves(@RequestHeader("Authorization") String token) {

    }

    /**
     * 좋아요 클릭(토글 역할)
     * @param token 사용자 정보
     * @param picture_id 좋아요 클릭한 그림
     */
    @PostMapping("/love/{picture_id}")
    public void clickLoveToggle(@RequestHeader("Authorization") String token,
                                @PathVariable Long picture_id) {
        // 토큰을 통해서 유저 아이디를 찾는다.
        final Long userId = authorizeService.getAuthorization(token);
        // 먼저 유저아이디와 이미지아이디로 좋아요 여부 찾기
        userService.clickLoveToggle(userId, pictureId);
        // 기존에 좋아요가 눌렸다면 좋아요 취소

        // 기존에 좋아요가 없다면 좋아요
    }


}
