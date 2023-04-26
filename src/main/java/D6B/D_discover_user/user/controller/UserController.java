package D6B.D_discover_user.user.controller;

import D6B.D_discover_user.common.service.AuthorizeService;
import D6B.D_discover_user.user.controller.dto.UserExistInfoDto;
import D6B.D_discover_user.user.controller.dto.UserRequestDto;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    /**
     * 클라이언트단에서 header에 토큰을 넣어보낸다.
     * @param token Firebase에서 받은 token값
     * @return
     */
    @GetMapping("")
    public ResponseEntity<UserExistInfoDto> isMember(@RequestHeader("Authorization") String token) {
        final Long gId = authorizeService.getAuthorization(token);   // 토큰으로 userId를 얻는다.
        // 해당 gId로 인가
        if(authorizeService.isAuthorization(gId))
            return ResponseEntity.ok(UserExistInfoDto.from(gId));
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

    /**
     * 구글로그인으로 처음 회원이 접속할 때, 회원정보를 우리 DB에 저장하기 위한 Controller
     * @param token : Firebase를 통해서 받은 구글 토큰
     * @param userRequestDto : (구글로그인 후) g_id, g_mail, g_name, img_url을 받을 수 있다.
     * @return : 등록 후, 유저의 id를 반환한다.
     */
    @PostMapping("")
    public ResponseEntity<Object> enrollUserInformation(@RequestHeader("Authorization") String token,
                                                        @RequestBody UserRequestDto userRequestDto) {
        final Long userId = authorizeService.getAuthorization(token);
        if(userId.equals(UNAUTHORIZED_USER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(
                UserExistInfoDto.from(userService.enrollUser(UserRequestDto.to(userRequestDto, userId), token)));
    }

    /**
     * 좋아요 한 그림 id리스트를 반환
     * @param token
     */
//    @GetMapping("/love")
//    public ResponseEntity<Object> readUserLoves(@RequestHeader("Authorization") String token) {
//        // 유저 아이디를 찾기
//        final Long userId = authorizeService.getAuthorization(token);
//    }

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
        userService.clickLoveToggle(userId, picture_id);
    }
}
