package D6B.D_discover_user.user.controller;

import D6B.D_discover_user.common.dto.AuthResponse;
import D6B.D_discover_user.common.service.AuthorizeService;
import D6B.D_discover_user.user.controller.dto.*;
import D6B.D_discover_user.user.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
     * @param userReadRequestDto : (구글로그인 후) 프론트단에서 유저관련 정보를 받을 수 있다.
     */
    @PostMapping("")
    public void enrollUserInformation(@RequestHeader("Authorization") String idToken,
                                                        @RequestBody UserReadRequestDto userReadRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userReadRequestDto.getUid());
        if(authResponse.getIsUser()) {
            userService.enrollUser(authResponse.getDecodedToken());
        } else {
            log.info("없는 회원입니다.");
        }
    }

    /**
     * 유저 본인의 정보를 확인하는 Controller
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param uid : Firebase 통해 얻은 uid
     * @return : 유저의 세부정보를 반환
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @GetMapping("/{uid}")
    public ResponseEntity<UserInfoResponseDto> readUserInfo(@RequestHeader("Authorization") String idToken,
                                                            @PathVariable String uid) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if(authResponse.getIsUser()) {
            return ResponseEntity.ok(UserInfoResponseDto
                    .from(userService.findUserByUid(authResponse.getDecodedToken().getUid())));
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /**
     * 회원정보 수정(구글에서 안오는 정보 : 나이, 성별)
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param userUpdateRequestDto : 변경 혹은 추가할 정보
     * @return : 추가된 정보가 추가된 유저 정보 반환
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @PutMapping("")
    public ResponseEntity<UserInfoResponseDto> updateUserInfos(@RequestHeader("Authorization") String idToken,
                                                               @RequestBody UserUpdateRequestDto userUpdateRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userUpdateRequestDto.getUid());
        if(authResponse.getIsUser()) {
            return ResponseEntity.ok(UserInfoResponseDto
                    .from(userService.updateUserInfos(authResponse.getDecodedToken(), userUpdateRequestDto)));
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 회원 이미지 수정
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param userImgUpdateRequestDto : 유저 이미지 수정 시 RequestDto
     */
    @PutMapping("/image")
    public ResponseEntity<Object> uploadImage(@RequestHeader("Authorization") String idToken,
                              @RequestBody UserImgUpdateRequestDto userImgUpdateRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userImgUpdateRequestDto.getUid());
        if(authResponse.getIsUser()) {
            userService.updateUserImg(userImgUpdateRequestDto);
            return ResponseEntity.ok().build();
        }
        else {
            log.info("해당 회원에 대한 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }


    /**
     * 회원탈퇴
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param uid : Firebase 통해 얻은 uid
     * @return : 삭제 여부를 판단해서 반환
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @DeleteMapping("/{uid}")
    public ResponseEntity<Object> deleteUserInfo(@RequestHeader("Authorization") String idToken,
                                                 @PathVariable String uid) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if(authResponse.getIsUser()) {
            userService.deleteUserInfo(authResponse.getDecodedToken());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * 좋아요
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param loveToggleRequestDto : 좋아요
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @PostMapping("/like")
    public void toggleLove(@RequestHeader("Authorization") String idToken,
                                              @RequestBody LoveToggleRequestDto loveToggleRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, loveToggleRequestDto.getUid());
        if(authResponse.getIsUser()) userService.toggleLove(loveToggleRequestDto);
    }

    /**
     * 다른 사람의 유저정보 조회
     * @param idToken : Firebase 통해서 받은 해당 유저에 대한 idToken
     * @param uid : Firebase 통해 얻은 uid
     * @param other_uid : 찾고자하는 유저의 uid
     * @return : 다른 사람의 유저 정보
     * @throws IOException : 에러
     * @throws FirebaseAuthException : 에러
     */
    @GetMapping("/{uid}/{other_uid}")
    public ResponseEntity<UserInfoResponseDto> readOtherUserInfo(@RequestHeader("Authorization") String idToken,
                                                                      @PathVariable String uid,
                                                                      @PathVariable String other_uid) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if(authResponse.getIsUser()) {
            return ResponseEntity.ok(UserInfoResponseDto.from(userService.findUserByUid(other_uid)));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 로그인 한 유저가 본인 또는 다른 유저의 좋아요를 누른 사진 가져오기
     * 본인일 경우와 아닐 경우를 분기해야 한다.
     * @param uid : Firebase 통해 얻은 uid
     * @return : 유저가 좋아요를 누른 그림의 id와 url
     */
    @GetMapping("/{uid}/like_picture/certified")
    public ResponseEntity<List<UserPicsResponseDto>> readUserLovePicsCertified(@RequestHeader("Authorization") String idToken,
                                                                               @PathVariable String uid,
                                                                               @RequestBody UserLikePictureRequestDto userLikePictureRequestDto) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, userLikePictureRequestDto.getUid());
        if(authResponse.getIsUser()) {
            FirebaseToken decodedToken = authResponse.getDecodedToken();
            // 본인이 본인의 좋아요 누른 사진을 보는 경우
            if(Objects.equals(authResponse.getDecodedToken().getUid(), uid)) {
                return ResponseEntity.ok(userService.findMyLovePics(decodedToken));
            // 타인의 좋아요 누른 사진을 보는 경우
            } else {
                return ResponseEntity.ok(userService.findUserLovePicsCertified(decodedToken, uid));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }


    @GetMapping("/{uid}/like_picture")
    public ResponseEntity<List<UserPicsResponseDto>> readUserLovePicsNotCert(@PathVariable String uid) {
        return ResponseEntity.ok(userService.findUserLovePics(uid));
    }

    /**
     * 유저가 만든 사진 가져오기
     * @param uid : Firebase 통해 얻은 uid
     * @return : 유저가 만든 이미지 정보
     */
    @GetMapping("{uid}/made_picture")
    public ResponseEntity<List<UserPicsResponseDto>> readUserMadePics(@PathVariable String uid){
        return ResponseEntity.ok(userService.findUserMadePics(uid));
    }



    //*******************************************************************************************************

    @GetMapping("/find_id_by_uid/{uid}")
    public Long findIdByUid(@PathVariable String uid) {
        return userService.findIdByUid(uid);
    }

    //***************************************여기서부턴 MSA 통신***********************************************//
    @PostMapping("/like/delete/{picture_id}")
    public ResponseEntity<Object> deleteLoveByPictureDead(@PathVariable Long picture_id) {
        userService.deActiveLove(picture_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/find_love_check_maker_name")
    public List<LoveCheckAndMakerResponseDto> findLoveChecksAndMakers(@RequestBody List<LoveCheckAndMakerRequestDto> loveCheckAndMakerRequestDtos) {
        return userService.findLoveChecksAndMakers(loveCheckAndMakerRequestDtos);
    }

    @GetMapping("/find_maker_name")
    public List<String> findMakers(@RequestBody List<String> makerUids) {
        return userService.findMakers(makerUids);
    }
}
