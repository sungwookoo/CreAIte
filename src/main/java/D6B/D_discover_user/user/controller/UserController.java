package D6B.D_discover_user.user.controller;

import D6B.D_discover_user.common.dto.AuthResponse;
import D6B.D_discover_user.common.service.AuthorizeService;
import D6B.D_discover_user.user.controller.dto.LoveRequestDto;
import D6B.D_discover_user.user.controller.dto.UserDetailsResponseDto;
import D6B.D_discover_user.user.controller.dto.UserRequestDto;
import D6B.D_discover_user.user.controller.dto.UserUpdateRequestDto;
import D6B.D_discover_user.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * 구글로그인으로 처음 회원이 접속할 때, 회원정보를 우리 DB에 저장하기 위한 Controller
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param userRequestDto : (구글로그인 후) 프론트단에서 유저관련 정보를 받을 수 있다.
     */
    @PostMapping("")
    public void enrollUserInformation(@RequestHeader("Authorization") String idToken,
                                                        @RequestBody UserRequestDto userRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userRequestDto.getUid());
        if(authResponse.getIsUser()) {
            userService.enrollUser(authResponse.getDecodedToken());
        } else {
            log.info("없는 회원입니다.");
        }
    }

    /**
     * 유저의 정보를 확인하는 Controller
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param uid : Firebase 통해 얻은 uid
     * @return : 유저의 세부정보를 반환
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @GetMapping("/{uid}")
    public ResponseEntity<UserDetailsResponseDto> readUserDetail(@RequestHeader("Authorization") String idToken,
                                                                 @PathVariable String uid) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if(authResponse.getIsUser()) {
            return ResponseEntity.ok(UserDetailsResponseDto
                    .from(userService.findUserByUid(authResponse.getDecodedToken().getUid())));
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    //***** 유저 정보는 이제 DB에서 온전히 갖고 있기로 한다. ******//
    /**
     * 유저의 정보를 변경하는 Controller(구글에서 안오는 정보 : 나이, 성별, 번호)
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param userUpdateRequestDto : 변경 혹은 추가할 정보
     * @return : 추가된 정보가 추가된 유저 정보 반환
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @PutMapping("")
    public ResponseEntity<UserDetailsResponseDto> updateUserInfos(@RequestHeader("Authorization") String idToken,
                                                                    @RequestBody UserUpdateRequestDto userUpdateRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userUpdateRequestDto.getUid());
        if(authResponse.getIsUser()) {
            return ResponseEntity.ok(UserDetailsResponseDto
                    .from(userService.updateUserInfos(authResponse.getDecodedToken(), userUpdateRequestDto)));
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("{uid}")
    public ResponseEntity<Object> deleteUserInfo(@RequestHeader("Authorization") String idToken,
                                                 @PathVariable String uid) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if(authResponse.getIsUser()) {
            userService.deleteUserInfo(authResponse.getDecodedToken());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 똑같은 그림으로 또 누른다면??? -> able로 바꾸기
    @PostMapping("/like")
    public void toggleLove(@RequestHeader("Authorization") String idToken,
                                              @RequestBody LoveRequestDto loveRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, loveRequestDto.getUid());
        if(authResponse.getIsUser()) userService.toggleLove(loveRequestDto.getUid(), loveRequestDto.getPicture_id());
    }

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
