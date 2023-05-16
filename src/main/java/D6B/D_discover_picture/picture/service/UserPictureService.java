package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureDetailResponse;
import D6B.D_discover_picture.picture.domain.Picture;
import D6B.D_discover_picture.picture.domain.PictureRepository;
import D6B.D_discover_picture.picture.domain.PictureTagRepository;
import D6B.D_discover_picture.picture.domain.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserPictureService {
    private final PictureRepository pictureRepository;
    private final PictureService pictureService;

    @Autowired
    public UserPictureService(PictureRepository pictureRepository,
                              PictureService pictureService) {
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
    }

    // 해당 그림의 좋아요 수 올리고 그림 이미지 주소 반환
    public String plusCount(Long pictureId) {
        Picture picture = pictureService.findPictureById(pictureId);
        if (picture.getIsAlive() == Boolean.TRUE) {
            picture.setLoveCount(picture.getLoveCount() + 1);
            pictureRepository.save(picture);
            return picture.getImgUrl();
        } else {
            throw new IllegalStateException("삭제된 이미지 입니다.");
        }
    }

    // 해당 그림의 좋아요 수 내리기
    public void minusCount(Long pictureId) {
        Picture picture = pictureService.findPictureById(pictureId);
        if (picture.getIsAlive() == Boolean.TRUE) {
            if (picture.getLoveCount() > 0) {
                picture.setLoveCount(picture.getLoveCount() - 1);
            } else {
                picture.setLoveCount(0L);
            }
            pictureRepository.save(picture);
            return;
        } else {
            throw new IllegalStateException("삭제된 이미지 입니다.");
        }

    }

    // 본인이 좋아요 한 이미지들 반환
    public List<PictureDetailResponse> getLikeAllList(List<Long> pictureIds) {
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Long id : pictureIds) {
            Picture picture = pictureService.findPictureById(id);
            if (picture.getIsAlive() == Boolean.TRUE) {
                List<String> tagWords = pictureService.getTagWords(picture);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, true);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    // 다른 사람의 좋아요 한 이미지 반환
    public List<PictureDetailResponse> getLikePublicList(List<Long> pictureIds) {
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Long id : pictureIds) {
            Picture picture = pictureService.findPictureById(id);
            if (picture.getIsAlive() == Boolean.TRUE && picture.getIsPublic() == Boolean.TRUE) {
                List<String> tagWords = pictureService.getTagWords(picture);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    // 내가 만든 이미지들 반환
    public List<PictureDetailResponse> getPicMadeByMe(String uid) {
        List<Picture> pictureList = pictureRepository.findAllByMakerUid(uid);
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Picture picture : pictureList) {
            if (picture.getIsAlive() == Boolean.TRUE) {
                List<String> tagWords = pictureService.getTagWords(picture);
                PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
                list.add(pictureDetailResponse);
            }
        }
        return list;
    }

    // 다른 사람이 만든 이미지들 반환
    public List<PictureDetailResponse> getPicMadeByOther(String uid) {
        List<Picture> pictureList = pictureRepository.findAllByMakerUidAndIsPublicAndIsAlive(uid, true, true);
        List<PictureDetailResponse> list = new ArrayList<>();
        for (Picture picture : pictureList) {
            List<String> tagWords = pictureService.getTagWords(picture);
            PictureDetailResponse pictureDetailResponse = PictureDetailResponse.from(picture, tagWords, false);
            list.add(pictureDetailResponse);
        }
        return list;
    }

    // 유저 탈퇴 시 유저가 제작한 이미지들 삭제 및 좋아요 누른 그림들 count 낮추기
    public void deleteUser(DeleteUserRequest deleteUserRequest) {
        List<Picture> userPicture = pictureRepository.findAllByMakerUidAndIsAlive(deleteUserRequest.getUid(), true);
        for (Picture picture : userPicture) {
            picture.setIsAlive(false);
            pictureService.minusTagCount(picture);
            pictureRepository.save(picture);
            // msa 요청
            MsaService.deleteLikeAlarmRequest(picture.getId());
            MsaService.deleteLikeRequest(picture.getId());
        }

        List<Long> pictureIdxs = deleteUserRequest.getPictureIdxs();
        for (Long pId : pictureIdxs) {
            Picture picture = pictureService.findPictureById(pId);
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
}
