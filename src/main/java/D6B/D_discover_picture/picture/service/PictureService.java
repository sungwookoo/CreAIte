package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.*;
import D6B.D_discover_picture.picture.service.dto.PictureIdUrlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static D6B.D_discover_picture.common.ConstValues.*;

@Slf4j
@Service
public class PictureService {
    private final PictureRepository pictureRepository;
    private final PictureTagRepository pictureTagRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository,
                          PictureTagRepository pictureTagRepository,
                          TagRepository tagRepository) {
        this.pictureRepository = pictureRepository;
        this.pictureTagRepository = pictureTagRepository;
        this.tagRepository = tagRepository;
    }

    public void savePicture(PictureSaveRequest pictureSaveRequest) {
        // 이미지 저장
        Picture picture = Picture.from(pictureSaveRequest);
        Picture newPicture = pictureRepository.save(picture);
        // 이미지에 달린 태그들.
        List<String> image_tags = pictureSaveRequest.getImageTags();
        for (String tag: image_tags) {
            Optional<Tag> opTag = tagRepository.findByWord(tag);
            Tag itag;
            if (opTag.isPresent()) {
                // 태그가 이미 존재한다면 그냥 들고오기
                itag = opTag.get();
            } else {
                // 없으면 새로 저장하고 가져오기
                Tag ntag = new Tag();
                ntag.setWord(tag);
                itag = tagRepository.save(ntag);
            }
            pictureTagRepository.save(PictureTag
                    .builder()
                    .tag(itag)
                    .picture(newPicture)
                    .build()
            );
        }
    }

    public void deletePicture(Long pictureId, String uid) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getMakerUid().equals(uid)) {
                picture.setIsAlive(Boolean.FALSE);
                pictureRepository.save(picture);
                // 해당 이미지의 좋아요 삭제 요청, 알림 삭제 요청 보내야함
                deleteLikeRequest(pictureId);
                deleteLikeAlarmRequest(pictureId);
                return;
            } else {
                throw new IllegalStateException("이미지를 올린 사람이 아닙니다.");
            }
        }
        throw new IllegalStateException("없는 이미지 입니다.");
    }

    // loveCount 올리기
    public String plusCount(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getIsAlive() == Boolean.TRUE) {
                picture.setLoveCount(picture.getLoveCount() + 1);
                pictureRepository.save(picture);
                return picture.getImgUrl();
            } else {
                throw new IllegalStateException("삭제된 이미지입니다.");
            }
        }
        throw new IllegalStateException("해당 이미지가 없습니다.");
    }

    // 좋아요 취소 시 count 낮추기
    public void minusCount(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            Picture picture = opPicture.get();
            if (picture.getIsAlive() == Boolean.TRUE) {
                if (picture.getLoveCount() > 0) {
                    picture.setLoveCount(picture.getLoveCount() - 1);
                } else {
                    picture.setLoveCount(0L);
                }
                pictureRepository.save(picture);
                return;
            } else {
                throw new IllegalStateException("삭제된 이미지입니다.");
            }
        }
        throw new IllegalStateException("해당 이미지가 없습니다.");
    }

    public void deleteUser(DeleteUserRequest deleteUserRequest) {
        // uid 바탕으로 유저의 이미지 삭제 (수정 필요. 위의 deletePicture를 통해 지울때 유저와 알림쪽에 요청도 보내야 하기 때문)
        List<Picture> userPicture = pictureRepository.findAllByMakerUid(deleteUserRequest.getUid());
        for (Picture picture : userPicture) {
            picture.setIsAlive(Boolean.FALSE);
            pictureRepository.save(picture);
            // 해당 이미지에 대한 좋아요와 알림 삭제 요청
            deleteLikeAlarmRequest(picture.getId());
            deleteLikeRequest(picture.getId());
        }

        // 이미지 좋아요 낮추기
        List<Long> pictures = deleteUserRequest.getPictureIdxs();
        for (Long pictureId : pictures) {
            Picture picture = findPictureById(pictureId);
            if (picture.getIsAlive() == Boolean.TRUE) {
                if (picture.getLoveCount() > 0) {
                    picture.setLoveCount(picture.getLoveCount() - 1);
                } else {
                    picture.setLoveCount(0L);
                }
                pictureRepository.save(picture);
            }
        }
    }

    public Picture findPictureById(Long pictureId) {
        Optional<Picture> opPicture = pictureRepository.findById(pictureId);
        if (opPicture.isPresent()) {
            return opPicture.get();
        } else {
            throw new IllegalStateException("해당 이미지 없음");
        }
    }

    // 해당 그림의 좋아요 삭제 요청, 추후 유저 서버와 협의 필요
    public void deleteLikeRequest(Long pictureId) {
        try {
            USER_SERVER_CLIENT.post()
                    .uri("/user")
                    .bodyValue(pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 해당 그림의 좋아요 알림을 삭제하는 요청, 추후 알림 서버와 협의 필요
    public void deleteLikeAlarmRequest(Long pictureId) {
        try {
            ALARM_SERVER_CLIENT.post()
                    .uri("/alarm")    /// uri 협의 필요
                    .bodyValue(pictureId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(RuntimeException::new))
                    .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(RuntimeException::new))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 그림 id들 받아서 url과 매칭시키고 반환하는거
    // user와 협의 필요
    public List<PictureIdUrlResponse> matchIdAndUrl(List<Long> ids) {
        List<PictureIdUrlResponse> list = new ArrayList<>();
        for (Long id: ids) {
            Picture picture = findPictureById(id);
            PictureIdUrlResponse pictureIdUrlResponse = new PictureIdUrlResponse();
            pictureIdUrlResponse.setPictureId(picture.getId());
            pictureIdUrlResponse.setImgUrl(picture.getImgUrl());
            list.add(pictureIdUrlResponse);
        }

        return list;
    }
}
