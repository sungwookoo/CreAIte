package D6B.D_discover_user.user.controller;

//import D6B.D_discover_user.common.service.AuthorizeService;
import D6B.D_discover_user.user.controller.dto.UserDetailsDto;
import D6B.D_discover_user.user.controller.dto.UserExistInfoDto;
import D6B.D_discover_user.user.controller.dto.UserRequestDto;
import D6B.D_discover_user.user.controller.dto.UserUpdateRequestDto;
import D6B.D_discover_user.user.domain.User;
import D6B.D_discover_user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//import static D6B.D_discover_user.common.ConstValues.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
//    private final AuthorizeService authorizeService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
//        this.authorizeService = authorizeService;
    }

//    /**
//     * 해당 유저가 회원인지 아닌지를 판단하는 함수 -> UI 구성이 달라질 때 사용하는 API Controller
//     * @param token : 파이어베이스에서 받은 token
//     * @return : 로그인한 유저의 id를 보낸다.
//     */
//    @GetMapping("")
//    public ResponseEntity<UserExistInfoDto> isMember(@RequestHeader("Authorization") String token) {
//        // 토큰으로 userId를 얻는다.
//        final Long userId = authorizeService.getAuthorization(token);
//        // 해당 userId로 인가
//        if(authorizeService.isAuthorization(userId))
//            return ResponseEntity.ok(UserExistInfoDto.from(userId));
//        else
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    /**
     * 구글로그인으로 처음 회원이 접속할 때, 회원정보를 우리 DB에 저장하기 위한 Controller
     * @param token : Firebase 통해서 받은 구글 토큰
     * @param userRequestDto : (구글로그인 후) g_id, g_mail, g_name, img_url 받을 수 있다.
     * @return : 등록 후, 유저의 id를 반환한다.
     */
    @PostMapping("")
    public ResponseEntity<UserExistInfoDto> enrollUserInformation(@RequestHeader("Authorization") String token,
                                                        @RequestBody UserRequestDto userRequestDto) {
        // token 유무만 판단한다.
        if(!token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else {
            return ResponseEntity.ok(UserExistInfoDto.from(userService.enrollUser(userRequestDto)));
        }
    }

//    /**
//     * 특정한 회원의 정보를 반환한다.(본인 or 찾는 사람의 정보)
//     * @param token : Firebase 통해서 받은 구글 토큰
//     * @param user_id : 정보를 찾고자 하는 유저의 아이디 값
//     * @return : 해당 유저의 정보를 반환한다.
//     */
//    @GetMapping("/{user_id}")
//    public ResponseEntity<UserDetailsDto> readUserDetails(@RequestHeader("Authorization") String token,
//                                                          @PathVariable Long user_id) {
//        final Long userId = authorizeService.getAuthorization(token);
//        // 토큰인증이 실패할 경우, unauthorized 반환하도록 함
//        if (userId.equals(UNAUTHORIZED_USER)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } else {
//            return ResponseEntity.ok(UserDetailsDto.from(userService.findUserByUserId(user_id)));
//        }
//    }
//
//    /**
//     * 로그인한 유저 본인의 정보를 수정하는 Controller
//     * @param token : Firebase 통해서 받은 구글 토큰
//     * @param user_id : 수정하고자 하는 유저의 아이디 값(본인일 경우)
//     * @param userUpdateRequestDto : 수정할 정보
//     * @return : 변경된 유저의 정보를 반환한다.
//     */
//    @PutMapping("/{user_id}")
//    public ResponseEntity<UserDetailsDto> updateUserDetails(@RequestHeader("Authorization") String token,
//                                                            @PathVariable Long user_id,
//                                                            @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
//        final Long userId = authorizeService.getAuthorization(token);
//        // 토큰인증이 실패할 경우, unauthorized 반환하도록 함
//        if(userId.equals(UNAUTHORIZED_USER)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } else if (!userId.equals(user_id)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } else {
//            return ResponseEntity.ok(UserDetailsDto.from(userService.updateUserDetails(user_id, userUpdateRequestDto)));
//        }
//    }
//
//
//    /**
//     * 좋아요 한 그림 id리스트를 반환
//     * @param token
//     */
////    @GetMapping("/love")
////    public ResponseEntity<Object> readUserLoves(@RequestHeader("Authorization") String token) {
////        // 유저 아이디를 찾기
////        final Long userId = authorizeService.getAuthorization(token);
////    }
//
//    /**
//     * 좋아요 클릭(토글 역할)
//     * @param token 사용자 정보
//     * @param picture_id 좋아요 클릭한 그림
//     */
//    @PostMapping("/love/{picture_id}")
//    public void clickLoveToggle(@RequestHeader("Authorization") String token,
//                                @PathVariable Long picture_id) {
//        // 토큰을 통해서 유저 아이디를 찾는다.
//        final Long userId = authorizeService.getAuthorization(token);
//        // 먼저 유저아이디와 이미지아이디로 좋아요 여부 찾기
//        userService.clickLoveToggle(userId, picture_id);
//    }
}
