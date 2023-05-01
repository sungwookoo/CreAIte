package D6B.D_discover_picture.picture.controller;

import D6B.D_discover_picture.common.dto.AuthResponse;
import D6B.D_discover_picture.common.service.AuthorizeService;
import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureDetailResponse;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.*;
import D6B.D_discover_picture.picture.service.PictureService;
import D6B.D_discover_picture.picture.service.exceptions.DeletePictureFailException;
import D6B.D_discover_picture.picture.service.exceptions.PictureNotSavedException;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    private final PictureRepository pictureRepository;
    private final PictureTagRepository pictureTagRepository;
    private final TagRepository tagRepository;
    private final PictureService pictureService;
    private final AuthorizeService authorizeService;

    @Autowired
    public PictureController(PictureRepository pictureRepository, PictureTagRepository pictureTagRepository,
                             TagRepository tagRepository, PictureService pictureService, AuthorizeService authorizeService) {
        this.pictureRepository = pictureRepository;
        this.pictureTagRepository = pictureTagRepository;
        this.tagRepository = tagRepository;
        this.pictureService = pictureService;
        this.authorizeService = authorizeService;
    }

    /*
    * 이미지 결과물 저장
    * @param pictureSaveRequest 이미지 저장에 필요한 정보 요청
    * @return 성공 여부
    * */
    @PostMapping("")
    public ResponseEntity<Boolean> savePicture(
            @RequestHeader("Authorization") String idToken,
            @RequestBody PictureSaveRequest pictureSaveRequest) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, pictureSaveRequest.getUid());
        if (authResponse.getIsUser()) {
            try {
                pictureService.savePicture(pictureSaveRequest);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } catch (PictureNotSavedException e) {
                log.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 이미지 삭제
    @DeleteMapping("/{picture_id}/{uid}")
    public ResponseEntity<Object> deletePicture(
            @RequestHeader("Authorization") String idToken,
            @PathVariable String uid,
            @PathVariable Long picture_id) throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if (authResponse.getIsUser()) {
            try {
                pictureService.deletePicture(picture_id, uid);
                return ResponseEntity.ok().build();
            } catch (DeletePictureFailException e) {
                log.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 이미지 detail
    @GetMapping("/noUser/{pictureId}")
    public ResponseEntity<PictureDetailResponse> readPictureDetail(
            @PathVariable Long pictureId) {
        try {
            Picture picture = pictureService.findPictureById(pictureId);
            if (picture.getIsAlive() == Boolean.TRUE && picture.getIsPublic() == Boolean.TRUE) {
                Set<PictureTag> tags = picture.getPictureTags();
                List<String> tagWords = new ArrayList<>();
                for (PictureTag pTag : tags) {
                    tagWords.add(pTag.getTag().getWord());
                }
                Collections.sort(tagWords);
                return ResponseEntity.ok(PictureDetailResponse.from(picture, tagWords));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/loginUser/{pictureId}/{uid}")
    public ResponseEntity<PictureDetailResponse> readPictureDetailWithLogin(
            @RequestHeader("Authorization") String idToken,
            @PathVariable Long pictureId, @PathVariable String uid)
            throws IOException, FirebaseAuthException {
        AuthResponse authResponse = authorizeService.isAuthorized(idToken, uid);
        if (authResponse.getIsUser()) {
            try {
                Picture picture = pictureService.findPictureById(pictureId);
                if (picture.getIsAlive() == Boolean.TRUE) {
                    if (picture.getIsPublic() == Boolean.FALSE && !picture.getMakerUid().equals(uid)) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    } else {
                        Set<PictureTag> tags = picture.getPictureTags();
                        List<String> tagWords = new ArrayList<>();
                        for (PictureTag pTag : tags) {
                            tagWords.add(pTag.getTag().getWord());
                        }
                        Collections.sort(tagWords);
                        return ResponseEntity.ok(PictureDetailResponse.from(picture, tagWords));
                    }
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // 이미지 좋아요 카운트 올리기
    @PostMapping("/create/count/{pictureId}")
    public ResponseEntity<String> plusLoveCount(
            @PathVariable Long pictureId) {
        try {
            String imgUrl = pictureService.plusCount(pictureId);
            return ResponseEntity.ok(imgUrl);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 좋아요 취소, 카운트 내리기
    @PostMapping("/delete/count/{pictureId}")
    public ResponseEntity<Object> minusLoveCount(
            @PathVariable Long pictureId) {
        try {
            pictureService.minusCount(pictureId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/delete/user")
    public ResponseEntity<Object> deleteUser(
            @RequestBody DeleteUserRequest deleteUserRequest) {
        try {
            pictureService.deleteUser(deleteUserRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
