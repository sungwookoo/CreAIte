package D6B.D_discover_picture.picture.service;

import D6B.D_discover_picture.picture.controller.dto.DeleteUserRequest;
import D6B.D_discover_picture.picture.controller.dto.PictureSaveRequest;
import D6B.D_discover_picture.picture.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
